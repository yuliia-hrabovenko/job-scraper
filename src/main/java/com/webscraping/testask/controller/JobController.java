package com.webscraping.testask.controller;

import com.webscraping.testask.dto.JobResponse;
import com.webscraping.testask.service.GoogleSheetsService;
import com.webscraping.testask.service.JobService;
import com.webscraping.testask.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final ScraperService scraperService;
    private final JobService jobService;
    private final GoogleSheetsService googleSheetsService;

    @PostMapping("/scrape/{jobFunction}")
    public void getJobsByJobFunction(@PathVariable("jobFunction") String jobFunction) {
        scraperService.renderPage(jobFunction);
    }

    @GetMapping("/jobs")
    public JobResponse filterJobs(@Param("postedDate") LocalDate postedDate,
                                  @Param("location") String location) {
        return new JobResponse(jobService.filterJobs(postedDate, location));
    }
}
