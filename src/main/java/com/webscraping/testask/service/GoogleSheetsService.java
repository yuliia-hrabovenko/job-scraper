package com.webscraping.testask.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.webscraping.testask.entity.JobEntity;
import com.webscraping.testask.util.SpreadSheetsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleSheetsService {

    private static final String SPREADSHEET_ID = "1nVDc8mvaIOchOiheJyntJnCDkEiMKMG7wbPXEuwzXao";
    public static final String READ_SHEET_RANGE = "Sheet1!A1:Z51";
    private static final String WRITE_SHEET_RANGE = "Sheet1!A1";
    private final Sheets sheets = SpreadSheetsUtil.getSheetsService();

    public List<List<Object>> fetchData() throws IOException {
        return sheets.spreadsheets().values().get(SPREADSHEET_ID, READ_SHEET_RANGE).execute().getValues();
    }

    public void saveData(List<JobEntity> jobs) {
        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("Position", "OrganizationUrl", "LogoUrl", "JobPageUrl", "OrganizationTitle",
                "JobFunction", "Location", "PostedDate", "Tags", "Description"));

        for (JobEntity job : jobs) {
            String description = nonNull(job.getDescription()) && job.getDescription().length() > 50000 ?
                    job.getDescription().substring(0, 50000) : job.getDescription();
            values.add(Arrays.asList(job.getPositionName(), job.getOrganizationUrl(), job.getLogoUrl(),
                    job.getJobPageUrl(), job.getOrganizationTitle(), job.getJobFunction(), job.getLocation().toString(),
                    job.getPostedDate(), job.getTags().toString(), description));
        }
        ValueRange body = new ValueRange().setValues(values);
        ClearValuesRequest requestBody = new ClearValuesRequest();

        try {
            sheets.spreadsheets().values()
                    .clear(SPREADSHEET_ID, "Sheet1", requestBody)
                    .execute();
            sheets.spreadsheets().values()
                    .update(SPREADSHEET_ID, WRITE_SHEET_RANGE, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Data has been saved to spreadsheets");
    }
}
