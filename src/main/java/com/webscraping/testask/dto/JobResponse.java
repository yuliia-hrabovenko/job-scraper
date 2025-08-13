package com.webscraping.testask.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JobResponse {
    private List<JobDto> jobs;
}
