
package com.sneha.jobapplicationtracker.controller;

import com.sneha.jobapplicationtracker.dto.JobApplicationRequest;
import com.sneha.jobapplicationtracker.dto.JobApplicationResponse;
import com.sneha.jobapplicationtracker.dto.PageResponse;

import com.sneha.jobapplicationtracker.enums.ApplicationStatus;
import com.sneha.jobapplicationtracker.service.JobApplicationService;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "companyName",
            "jobTitle",
            "location",
            "jobType",
            "status",
            "appliedDate",
            "salary"
    );

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<JobApplicationResponse> saveApplication(
            @Valid @RequestBody JobApplicationRequest request) {

        JobApplicationResponse savedApplication =
                jobApplicationService.saveJobApplication(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedApplication.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedApplication);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllApplications() {

        return ResponseEntity.ok(
                jobApplicationService.getAllApplications()
        );
    }

    // READ ALL WITH PAGINATION AND SORTING
    @GetMapping("/page")
    public ResponseEntity<PageResponse<JobApplicationResponse>> getApplicationsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appliedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = createPageable(page, size, sortBy, direction);

        return ResponseEntity.ok(
                jobApplicationService.getApplicationsPage(pageable)
        );
    }

    // SEARCH BY COMPANY NAME
    @GetMapping("/search")
    public ResponseEntity<List<JobApplicationResponse>> searchApplications(
            @RequestParam String companyName) {

        return ResponseEntity.ok(
                jobApplicationService.searchApplicationsByCompanyName(companyName)
        );
    }

    // PAGINATED SEARCH
    @GetMapping("/search/page")
    public ResponseEntity<PageResponse<JobApplicationResponse>> searchApplicationsPage(
            @RequestParam String companyName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "companyName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = createPageable(page, size, sortBy, direction);

        return ResponseEntity.ok(
                jobApplicationService.searchApplicationsPage(
                        companyName, pageable
                )
        );
    }

    // FILTER BY STATUS
    @GetMapping("/filter")
    public ResponseEntity<List<JobApplicationResponse>> filterApplications(
            @RequestParam String status) {

        ApplicationStatus applicationStatus =
                ApplicationStatus.fromValue(status);

        return ResponseEntity.ok(
                jobApplicationService.filterApplicationsByStatus(applicationStatus)
        );
    }

    // PAGINATED FILTER
    @GetMapping("/filter/page")
    public ResponseEntity<PageResponse<JobApplicationResponse>> filterApplicationsPage(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appliedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        ApplicationStatus applicationStatus =
                ApplicationStatus.fromValue(status);

        Pageable pageable = createPageable(page, size, sortBy, direction);

        return ResponseEntity.ok(
                jobApplicationService.filterApplicationsPage(
                        applicationStatus, pageable
                )
        );
    }

    // DASHBOARD STATISTICS
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStatistics() {

        return ResponseEntity.ok(
                jobApplicationService.getDashboardStatistics()
        );
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplicationById(
            @PathVariable Long id) {

        return jobApplicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request) {

        return jobApplicationService
                .updateJobApplication(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id) {

        boolean deleted = jobApplicationService.deleteApplication(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // CREATE PAGEABLE OBJECT
    private Pageable createPageable(
            int page, int size, String sortBy, String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field. Allowed fields: "
                            + ALLOWED_SORT_FIELDS
            );
        }

        if (!"asc".equalsIgnoreCase(direction)
                && !"desc".equalsIgnoreCase(direction)) {

            throw new IllegalArgumentException(
                    "Sort direction must be either asc or desc"
            );
        }

        Sort.Direction sortDirection =
                "asc".equalsIgnoreCase(direction)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
