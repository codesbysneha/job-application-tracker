
package com.sneha.jobapplicationtracker.service;

import com.sneha.jobapplicationtracker.dto.JobApplicationRequest;
import com.sneha.jobapplicationtracker.dto.JobApplicationResponse;
import com.sneha.jobapplicationtracker.dto.PageResponse;
import com.sneha.jobapplicationtracker.entity.JobApplication;
import com.sneha.jobapplicationtracker.enums.ApplicationStatus;
import com.sneha.jobapplicationtracker.repository.JobApplicationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private JobApplicationService jobApplicationService;


    // TEST 1: CREATE APPLICATION
    @Test
    void shouldCreateJobApplication() {

        JobApplicationRequest request = createRequest();

        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> {

                    JobApplication application = invocation.getArgument(0);

                    ReflectionTestUtils.setField(application, "id", 1L);

                    return application;
                });

        JobApplicationResponse response =
                jobApplicationService.saveJobApplication(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Google", response.getCompanyName());
        assertEquals("Java Developer", response.getJobTitle());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());

        verify(jobApplicationRepository, times(1))
                .save(any(JobApplication.class));
    }


    // TEST 2: GET ALL APPLICATIONS
    @Test
    void shouldGetAllApplications() {

        JobApplication application = createApplication(1L);

        when(jobApplicationRepository.findAll())
                .thenReturn(List.of(application));

        List<JobApplicationResponse> responses =
                jobApplicationService.getAllApplications();

        assertEquals(1, responses.size());
        assertEquals("Google", responses.get(0).getCompanyName());

        verify(jobApplicationRepository).findAll();
    }


    // TEST 3: GET APPLICATION BY ID
    @Test
    void shouldGetApplicationById() {

        JobApplication application = createApplication(1L);

        when(jobApplicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        Optional<JobApplicationResponse> response =
                jobApplicationService.getApplicationById(1L);

        assertTrue(response.isPresent());
        assertEquals("Google", response.get().getCompanyName());

        verify(jobApplicationRepository).findById(1L);
    }


    // TEST 4: UPDATE APPLICATION
    @Test
    void shouldUpdateJobApplication() {

        JobApplication existingApplication = createApplication(1L);

        JobApplicationRequest updatedRequest = createRequest();

        updatedRequest.setCompanyName("Microsoft");
        updatedRequest.setJobTitle("Backend Developer");
        updatedRequest.setStatus(ApplicationStatus.INTERVIEW);

        when(jobApplicationRepository.findById(1L))
                .thenReturn(Optional.of(existingApplication));

        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<JobApplicationResponse> response =
                jobApplicationService.updateJobApplication(
                        1L, updatedRequest
                );

        assertTrue(response.isPresent());
        assertEquals("Microsoft", response.get().getCompanyName());
        assertEquals("Backend Developer", response.get().getJobTitle());
        assertEquals(
                ApplicationStatus.INTERVIEW,
                response.get().getStatus()
        );

        verify(jobApplicationRepository).save(existingApplication);
    }


    // TEST 5: UPDATE APPLICATION THAT DOES NOT EXIST
    @Test
    void shouldReturnEmptyWhenUpdatingMissingApplication() {

        when(jobApplicationRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<JobApplicationResponse> response =
                jobApplicationService.updateJobApplication(
                        99L, createRequest()
                );

        assertTrue(response.isEmpty());

        verify(jobApplicationRepository, never())
                .save(any(JobApplication.class));
    }


    // TEST 6: SEARCH BY COMPANY NAME
    @Test
    void shouldSearchApplicationsByCompanyName() {

        JobApplication application = createApplication(1L);

        when(jobApplicationRepository
                .findByCompanyNameContainingIgnoreCase("Google"))
                .thenReturn(List.of(application));

        List<JobApplicationResponse> responses =
                jobApplicationService.searchApplicationsByCompanyName(
                        "Google"
                );

        assertEquals(1, responses.size());
        assertEquals("Google", responses.get(0).getCompanyName());

        verify(jobApplicationRepository)
                .findByCompanyNameContainingIgnoreCase("Google");
    }


    // TEST 7: FILTER BY STATUS
    @Test
    void shouldFilterApplicationsByStatus() {

        JobApplication application = createApplication(1L);

        when(jobApplicationRepository.findByStatus(
                ApplicationStatus.APPLIED
        )).thenReturn(List.of(application));

        List<JobApplicationResponse> responses =
                jobApplicationService.filterApplicationsByStatus(
                        ApplicationStatus.APPLIED
                );

        assertEquals(1, responses.size());
        assertEquals(
                ApplicationStatus.APPLIED,
                responses.get(0).getStatus()
        );

        verify(jobApplicationRepository)
                .findByStatus(ApplicationStatus.APPLIED);
    }


    // TEST 8: PAGINATION
    @Test
    void shouldGetApplicationsWithPagination() {

        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by("appliedDate").descending()
        );

        JobApplication application = createApplication(1L);

        Page<JobApplication> databasePage =
                new PageImpl<>(List.of(application), pageable, 1);

        when(jobApplicationRepository.findAll(pageable))
                .thenReturn(databasePage);

        PageResponse<JobApplicationResponse> response =
                jobApplicationService.getApplicationsPage(pageable);

        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(5, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());

        verify(jobApplicationRepository).findAll(pageable);
    }


    // TEST 9: DASHBOARD STATISTICS
    @Test
    void shouldGetDashboardStatistics() {

        when(jobApplicationRepository.count()).thenReturn(4L);

        when(jobApplicationRepository.countByStatus(
                ApplicationStatus.APPLIED
        )).thenReturn(2L);

        when(jobApplicationRepository.countByStatus(
                ApplicationStatus.INTERVIEW
        )).thenReturn(2L);

        when(jobApplicationRepository.countByStatus(
                ApplicationStatus.OFFERED
        )).thenReturn(0L);

        when(jobApplicationRepository.countByStatus(
                ApplicationStatus.REJECTED
        )).thenReturn(0L);

        when(jobApplicationRepository.countByStatus(
                ApplicationStatus.WITHDRAWN
        )).thenReturn(0L);

        Map<String, Long> statistics =
                jobApplicationService.getDashboardStatistics();

        assertEquals(4L, statistics.get("totalApplications"));
        assertEquals(2L, statistics.get("applied"));
        assertEquals(2L, statistics.get("interviews"));
        assertEquals(0L, statistics.get("offers"));
        assertEquals(0L, statistics.get("rejected"));
        assertEquals(0L, statistics.get("withdrawn"));
    }


    // TEST 10: DELETE EXISTING APPLICATION
    @Test
    void shouldDeleteExistingApplication() {

        when(jobApplicationRepository.existsById(1L))
                .thenReturn(true);

        boolean result =
                jobApplicationService.deleteApplication(1L);

        assertTrue(result);

        verify(jobApplicationRepository).deleteById(1L);
    }


    // TEST 11: DELETE NON-EXISTING APPLICATION
    @Test
    void shouldNotDeleteMissingApplication() {

        when(jobApplicationRepository.existsById(99L))
                .thenReturn(false);

        boolean result =
                jobApplicationService.deleteApplication(99L);

        assertFalse(result);

        verify(jobApplicationRepository, never())
                .deleteById(99L);
    }


    // TEST 12: PAGINATED COMPANY SEARCH
    @Test
    void shouldSearchApplicationsWithPagination() {

        Pageable pageable = PageRequest.of(0, 5);

        JobApplication application = createApplication(1L);

        Page<JobApplication> databasePage =
                new PageImpl<>(List.of(application), pageable, 1);

        when(jobApplicationRepository
                .findByCompanyNameContainingIgnoreCase(
                        "Google", pageable
                )).thenReturn(databasePage);

        PageResponse<JobApplicationResponse> response =
                jobApplicationService.searchApplicationsPage(
                        "Google", pageable
                );

        assertEquals(1, response.getContent().size());
        assertEquals("Google", response.getContent().get(0).getCompanyName());
    }


    // TEST 13: PAGINATED STATUS FILTER
    @Test
    void shouldFilterApplicationsWithPagination() {

        Pageable pageable = PageRequest.of(0, 5);

        JobApplication application = createApplication(1L);

        Page<JobApplication> databasePage =
                new PageImpl<>(List.of(application), pageable, 1);

        when(jobApplicationRepository.findByStatus(
                ApplicationStatus.APPLIED, pageable
        )).thenReturn(databasePage);

        PageResponse<JobApplicationResponse> response =
                jobApplicationService.filterApplicationsPage(
                        ApplicationStatus.APPLIED, pageable
                );

        assertEquals(1, response.getContent().size());
        assertEquals(
                ApplicationStatus.APPLIED,
                response.getContent().get(0).getStatus()
        );
    }


    // HELPER: CREATE REQUEST DTO
    private JobApplicationRequest createRequest() {

        JobApplicationRequest request = new JobApplicationRequest();

        request.setCompanyName("Google");
        request.setJobTitle("Java Developer");
        request.setLocation("Bangalore");
        request.setJobType("Full-time");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setAppliedDate(LocalDate.now());
        request.setJobUrl("https://careers.google.com");
        request.setSalary(new BigDecimal("75000"));
        request.setNotes("Test application");

        return request;
    }


    // HELPER: CREATE ENTITY
    private JobApplication createApplication(Long id) {

        JobApplication application = new JobApplication();

        ReflectionTestUtils.setField(application, "id", id);

        application.setCompanyName("Google");
        application.setJobTitle("Java Developer");
        application.setLocation("Bangalore");
        application.setJobType("Full-time");
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDate.now());
        application.setJobUrl("https://careers.google.com");
        application.setSalary(new BigDecimal("75000"));
        application.setNotes("Test application");

        return application;
    }
}
