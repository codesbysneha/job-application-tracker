
package com.sneha.jobapplicationtracker.repository;

import com.sneha.jobapplicationtracker.entity.JobApplication;
import com.sneha.jobapplicationtracker.enums.ApplicationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    // SEARCH BY COMPANY NAME
    List<JobApplication> findByCompanyNameContainingIgnoreCase(
            String companyName
    );

    // FILTER BY STATUS
    List<JobApplication> findByStatus(ApplicationStatus status);

    // COUNT APPLICATIONS BY STATUS
    long countByStatus(ApplicationStatus status);

    // PAGINATED SEARCH BY COMPANY NAME
    Page<JobApplication> findByCompanyNameContainingIgnoreCase(
            String companyName,
            Pageable pageable
    );

    // PAGINATED FILTER BY STATUS
    Page<JobApplication> findByStatus(
            ApplicationStatus status,
            Pageable pageable
    );
}
