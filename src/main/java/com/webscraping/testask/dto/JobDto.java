package com.webscraping.testask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class JobDto {

    @JsonProperty("job_page_url")
    private String jobPageUrl;

    @JsonProperty("position_name")
    private String positionName;

    @JsonProperty("organization_url")
    private String organizationUrl;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("organization_title")
    private String organizationTitle;

    @JsonProperty("job_function")
    private String jobFunction;

    @JsonProperty("posted_date")
    private Long postedDate;

    private List<String> location;

    private List<String> tags;

}
