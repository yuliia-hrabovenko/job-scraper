package com.webscraping.testask.dto;

import com.webscraping.testask.entity.JobEntity;

public final class JobMapper {

    public static JobDto toDto(JobEntity job) {
        return JobDto.builder()
                .jobFunction(job.getJobFunction())
                .tags(job.getTags())
                .jobPageUrl(job.getJobPageUrl())
                .logoUrl(job.getLogoUrl())
                .organizationUrl(job.getOrganizationUrl())
                .positionName(job.getPositionName())
                .postedDate(job.getPostedDate())
                .organizationTitle(job.getOrganizationTitle())
                .positionName(job.getPositionName())
                .location(job.getLocation())
                .build();
    }
}
