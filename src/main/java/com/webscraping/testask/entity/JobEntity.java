package com.webscraping.testask.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "jobs")
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_page_url", length = 350)
    private String jobPageUrl;

    @Column(name = "position_name")
    private String positionName;

    @Column(name = "organization_url", length = 350)
    private String organizationUrl;

    @Column(name = "logo_url", length = 350)
    private String logoUrl;

    @Column(name = "organization_title")
    private String organizationTitle;

    @Column(name = "job_function")
    private String jobFunction;

    @Column(name = "posted_date")
    private Long postedDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private List<String> location;

    private List<String> tags;
}
