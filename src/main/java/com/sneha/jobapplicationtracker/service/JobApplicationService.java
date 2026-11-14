
package com.sneha.jobapplicationtracker.service;

import com.sneha.jobapplicationtracker.dto.JobApplicationRequest;
import com.sneha.jobapplicationtracker.dto.JobApplicationResponse;
import com.sneha.jobapplicationtracker.dto.PageResponse;

import com.sneha.jobapplicationtracker.entity.JobApplication;
import com.sneha.jobapplicationtracker.enums.ApplicationStatus;
import com.sneha.jobapplicationtracker.repository.JobApplicationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
@Transactional(readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    // CREATE
    @Transactional
    public JobApplicationResponse saveJobApplication(
            JobApplicationRequest request) {

        JobApplication application = toEntity(request);

        JobApplication savedApplication =
                jobApplicationRepository.save(application);

        return toResponse(savedApplication);
    }

    // READ ALL
    public List<JobApplicationResponse> getAllApplications() {

        return jobApplicationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ ALL WITH PAGINATION AND SORTING
    public PageResponse<JobApplicationResponse> getApplicationsPage(
            Pageable pageable) {

        Page<JobApplicationResponse> page =
                jobApplicationRepository.findAll(pageable)
                        .map(this::toResponse);

        return new PageResponse<>(page);
    }

    // READ BY ID
    public Optional<JobApplicationResponse> getApplicationById(Long id) {

        return jobApplicationRepository.findById(id)
                .map(this::toResponse);
    }

    // UPDATE
    @Transactional
    public Optional<JobApplicationResponse> updateJobApplication(
            Long id, JobApplicationRequest request) {

        Optional<JobApplication> existingApplication =
                jobApplicationRepository.findById(id);

        if (existingApplication.isPresent()) {

            JobApplication application = existingApplication.get();

            application.setCompanyName(request.getCompanyName());
            application.setJobTitle(request.getJobTitle());
            application.setLocation(request.getLocation());
            application.setJobType(request.getJobType());
            application.setStatus(request.getStatus());
            application.setAppliedDate(request.getAppliedDate());
            application.setJobUrl(request.getJobUrl());
            application.setSalary(request.getSalary());
            application.setNotes(request.getNotes());

            JobApplication updatedApplication =
                    jobApplicationRepository.save(application);

            return Optional.of(toResponse(updatedApplication));
        }

        return Optional.empty();
    }

    // SEARCH BY COMPANY NAME
    public List<JobApplicationResponse> searchApplicationsByCompanyName(
            String companyName) {

        return jobApplicationRepository
                .findByCompanyNameContainingIgnoreCase(companyName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // PAGINATED SEARCH BY COMPANY NAME
    public PageResponse<JobApplicationResponse> searchApplicationsPage(
            String companyName, Pageable pageable) {

        Page<JobApplicationResponse> page =
                jobApplicationRepository
                        .findByCompanyNameContainingIgnoreCase(
                                companyName, pageable
                        )
                        .map(this::toResponse);

        return new PageResponse<>(page);
    }

    // FILTER BY STATUS
    public List<JobApplicationResponse> filterApplicationsByStatus(
            ApplicationStatus status) {

        return jobApplicationRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // PAGINATED FILTER BY STATUS
    public PageResponse<JobApplicationResponse> filterApplicationsPage(
            ApplicationStatus status, Pageable pageable) {

        Page<JobApplicationResponse> page =
                jobApplicationRepository.findByStatus(status, pageable)
                        .map(this::toResponse);

        return new PageResponse<>(page);
    }

    // GET TOTAL APPLICATIONS
    public long getTotalApplications() {
        return jobApplicationRepository.count();
    }

    // GET APPLICATION COUNT BY STATUS
    public long getApplicationsCountByStatus(ApplicationStatus status) {
        return jobApplicationRepository.countByStatus(status);
    }

    // DASHBOARD STATISTICS
    public Map<String, Long> getDashboardStatistics() {

        Map<String, Long> statistics = new LinkedHashMap<>();

        statistics.put("totalApplications", getTotalApplications());

        statistics.put(
                "applied",
                getApplicationsCountByStatus(ApplicationStatus.APPLIED)
        );

        statistics.put(
                "interviews",
                getApplicationsCountByStatus(ApplicationStatus.INTERVIEW)
        );

        statistics.put(
                "offers",
                getApplicationsCountByStatus(ApplicationStatus.OFFERED)
        );

        statistics.put(
                "rejected",
                getApplicationsCountByStatus(ApplicationStatus.REJECTED)
        );

        statistics.put(
                "withdrawn",
                getApplicationsCountByStatus(ApplicationStatus.WITHDRAWN)
        );

        return statistics;
    }

    // DELETE
    @Transactional
    public boolean deleteApplication(Long id) {

        if (!jobApplicationRepository.existsById(id)) {
            return false;
        }

        jobApplicationRepository.deleteById(id);

        return true;
    }

    // CONVERT REQUEST DTO TO ENTITY
    private JobApplication toEntity(JobApplicationRequest request) {

        JobApplication application = new JobApplication();

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setLocation(request.getLocation());
        application.setJobType(request.getJobType());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());
        application.setJobUrl(request.getJobUrl());
        application.setSalary(request.getSalary());
        application.setNotes(request.getNotes());

        return application;
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private JobApplicationResponse toResponse(JobApplication application) {

        return new JobApplicationResponse(
                application.getId(),
                application.getCompanyName(),
                application.getJobTitle(),
                application.getLocation(),
                application.getJobType(),
                application.getStatus(),
                application.getAppliedDate(),
                application.getJobUrl(),
                application.getSalary(),
                application.getNotes()
        );
    }
}
