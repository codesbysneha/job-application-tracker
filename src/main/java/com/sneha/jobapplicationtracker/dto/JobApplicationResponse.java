
package com.sneha.jobapplicationtracker.dto;

import com.sneha.jobapplicationtracker.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class JobApplicationResponse {

    private Long id;
    private String companyName;
    private String jobTitle;
    private String location;
    private String jobType;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String jobUrl;
    private BigDecimal salary;
    private String notes;

    public JobApplicationResponse(
            Long id,
            String companyName,
            String jobTitle,
            String location,
            String jobType,
            ApplicationStatus status,
            LocalDate appliedDate,
            String jobUrl,
            BigDecimal salary,
            String notes) {

        this.id = id;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.location = location;
        this.jobType = jobType;
        this.status = status;
        this.appliedDate = appliedDate;
        this.jobUrl = jobUrl;
        this.salary = salary;
        this.notes = notes;
    }

    // GETTERS

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public String getJobType() {
        return jobType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public String getNotes() {
        return notes;
    }
}
