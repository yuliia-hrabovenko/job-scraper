package com.webscraping.testask.service;

import com.webscraping.testask.dto.JobDto;
import com.webscraping.testask.dto.JobMapper;
import com.webscraping.testask.entity.JobEntity;
import com.webscraping.testask.repository.JobRepository;
import com.webscraping.testask.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public List<JobDto> filterJobs(LocalDate postedDate, String location) {
        if (postedDate != null) {
            return findByPostedDate(postedDate);
        } else if (location != null) {
            return findByLocation(location);
        }
        return findAll();
    }

    private List<JobDto> findByPostedDate(LocalDate postedDate) {
        List<JobEntity> byPostedDate = jobRepository.findByPostedDate(DateUtil.localDateToUnix(postedDate));
        return byPostedDate.stream().map(JobMapper::toDto).toList();
    }

    private List<JobDto> findByLocation(String location) {
        List<JobEntity> byLocation = jobRepository.findByLocation(location);
        return byLocation.stream().map(JobMapper::toDto).toList();
    }

    private List<JobDto> findAll() {
        List<JobEntity> allJobs = jobRepository.findAll();
        return allJobs.stream().map(JobMapper::toDto).toList();
    }
}
