package com.webscraping.testask.repository;

import com.webscraping.testask.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<JobEntity, Integer> {

    List<JobEntity> findByPostedDate(Long postedDate);

    @Query(value = "select * from jobs j where :locationParam = any(j.location)", nativeQuery = true)
    List<JobEntity> findByLocation(@Param("locationParam") String location);
}
