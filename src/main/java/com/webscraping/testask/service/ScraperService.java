package com.webscraping.testask.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.webscraping.testask.entity.JobEntity;
import com.webscraping.testask.repository.JobRepository;
import com.webscraping.testask.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor
@Slf4j
public class ScraperService {

    public static final String JOBS_TECHSTARS_COM = "https://jobs.techstars.com";
    private final OkHttpClient okHttpClient;
    private final JobRepository jobRepository;
    private final GoogleSheetsService sheetsService;

    public void renderPage(String jobFunction) {
        List<String> jobsHtml;
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );
            Page page = browser.newPage();
            page.navigate(JOBS_TECHSTARS_COM + "/jobs?q=" + jobFunction,
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            Locator jobLocator;
            int prevCount = 0;
            while (true) {
                Locator loadMoreButton = page.locator("button:has-text('Load More')");
                if (loadMoreButton.isVisible()) {
                    loadMoreButton.click();
                    page.waitForTimeout(5000); // allow new jobLocator to load
                } else {
                    page.locator(".sc-beqWaB.sc-gueYoa.iSijfj.MYFxR").scrollIntoViewIfNeeded();
                    page.waitForTimeout(6000); // wait for new jobs to load
                }

                jobLocator = page.locator("div[data-testid='job-list-item']");
                int currentCount = jobLocator.count();
                if (currentCount == prevCount) {
                    break;
                }
                prevCount = currentCount;
            }

            jobsHtml = jobLocator.all().stream()
                    .map(Locator::innerHTML)
                    .toList();

            browser.close();
        }
        processJobs(jobsHtml);
    }

    private JobEntity parseJob(String jobHtml) {

        Document jobDoc = Jsoup.parse(jobHtml);
        Element linkElem = jobDoc.selectFirst("a[data-testid='job-title-link']");
        String jobPageUrl = linkElem != null ? linkElem.attr("href") : "";

        List<String> tags = null;
        if (jobPageUrl.startsWith("/companies")) {
            Elements tagsElem = jobDoc.select("div[data-testid='tag']");
            tags = tagsElem.stream().map(Element::text).toList();
        } else {
            throw new RuntimeException("Public site job with url: " + jobPageUrl);
        }

        jobPageUrl = JOBS_TECHSTARS_COM + jobPageUrl;
        Request request = new Request.Builder()
                .url(jobPageUrl)
                .build();

        JobEntity jobEntity = null;
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.info("HTTP request failed: {} for url {}", response.code(), jobPageUrl);
            }

            String html = response.body().string();
            Document doc = Jsoup.parse(html);

            Element positionNameElem = doc.selectFirst("h2.sc-beqWaB.jqWDOR");
            String positionName = positionNameElem != null ? positionNameElem.text() : null;

            Element urlToOrganizationElem = doc.selectFirst("a[data-testid=button]");
            String organizationUrl = urlToOrganizationElem != null ? urlToOrganizationElem.attr("href") : null;

            Element logoUrlElem = doc.selectFirst("img[data-testid=image]");
            String logoUrl = logoUrlElem != null ? logoUrlElem.attr("src") : null;

            Element organizationTitleElem = doc.selectFirst("p.sc-beqWaB.bpXRKw");
            String organizationTitle = organizationTitleElem != null ? organizationTitleElem.text() : null;

            Element parentDiv = doc.selectFirst("div.sc-beqWaB.sc-gueYoa.dmdAKU.MYFxR");
            String jobFunction = null;
            List<String> location = null;
            Long postedDate = null;

            if (parentDiv != null) {
                Elements elements = parentDiv.select("div.sc-beqWaB.bpXRKw");

                jobFunction = !elements.isEmpty() ? elements.get(0).text() : null;
                location = elements.size() > 1 ?
                        Arrays.stream(elements.get(1).text().split("[,·]"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList() : List.of();

                Element postedDateDiv = parentDiv.selectFirst("div.sc-beqWaB.cczurT");
                try {
                    postedDate = postedDateDiv != null ? DateUtil.dateToUnix(postedDateDiv.text()) : null;
                } catch (DateTimeParseException e) {
                    log.info("Failed to parse date for url: {} with message: {} \n", jobPageUrl, e.getMessage());
                }

            } else {
                log.info("Parent div not found");
            }

            Element descriptionElem = doc.selectFirst("div[data-testid=careerPage]") != null ?
                    doc.selectFirst("div[data-testid=careerPage]") :
                    doc.selectFirst("div.sc-beqWaB.fmCCHr");
            String description = descriptionElem != null ? descriptionElem.html() : null;

            jobEntity = JobEntity.builder()
                    .jobPageUrl(jobPageUrl)
                    .positionName(positionName)
                    .organizationUrl(organizationUrl)
                    .logoUrl(logoUrl)
                    .organizationTitle(organizationTitle)
                    .jobFunction(jobFunction)
                    .postedDate(postedDate)
                    .description(description)
                    .tags(tags)
                    .location(location)
                    .build();

        } catch (IOException e) {
            log.info("Parsing job with url {} has failed with exception: {}", jobPageUrl, e.getMessage());
        }
        return jobEntity;
    }

    public void processJobs(List<String> jobsHtml) {
        ExecutorService executor = Executors.newFixedThreadPool(16);
        List<CompletableFuture<JobEntity>> futures = jobsHtml.stream()
                .map(html -> CompletableFuture.supplyAsync(() -> parseJob(html), executor)
                        .handle((result, ex) -> {
                            if (ex != null) {
                                log.info("Failed to parse the job: {}", ex.getMessage());
                                return null;
                            }
                            return result;
                        }))
                .toList();

        List<JobEntity> jobEntities = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
        executor.shutdown();

        jobRepository.saveAll(jobEntities);
        log.info("Entities are saved to the db");
        sheetsService.saveData(jobEntities);
    }
}