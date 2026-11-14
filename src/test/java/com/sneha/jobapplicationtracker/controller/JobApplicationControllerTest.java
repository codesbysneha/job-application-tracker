
package com.sneha.jobapplicationtracker.controller;

import com.sneha.jobapplicationtracker.dto.JobApplicationResponse;
import com.sneha.jobapplicationtracker.dto.PageResponse;
import com.sneha.jobapplicationtracker.enums.ApplicationStatus;
import com.sneha.jobapplicationtracker.exception.GlobalExceptionHandler;
import com.sneha.jobapplicationtracker.service.JobApplicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationControllerTest {

    @Mock
    private JobApplicationService jobApplicationService;

    @InjectMocks
    private JobApplicationController jobApplicationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(jobApplicationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    // TEST 1: CREATE APPLICATION - 201 CREATED
    @Test
    void shouldCreateApplication() throws Exception {

        JobApplicationResponse response =
                createResponse(1L, ApplicationStatus.APPLIED);

        when(jobApplicationService.saveJobApplication(
                any(com.sneha.jobapplicationtracker.dto.JobApplicationRequest.class)
        )).thenReturn(response);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))

                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        org.hamcrest.Matchers.containsString(
                                "/api/applications/1"
                        )
                ))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Google"));

        verify(jobApplicationService).saveJobApplication(any());
    }


    // TEST 2: GET ALL APPLICATIONS - 200 OK
    @Test
    void shouldGetAllApplications() throws Exception {

        when(jobApplicationService.getAllApplications())
                .thenReturn(List.of(
                        createResponse(1L, ApplicationStatus.APPLIED)
                ));

        mockMvc.perform(get("/api/applications"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].companyName").value("Google"));
    }


    // TEST 3: GET APPLICATION BY ID - 200 OK
    @Test
    void shouldGetApplicationById() throws Exception {

        when(jobApplicationService.getApplicationById(1L))
                .thenReturn(Optional.of(
                        createResponse(1L, ApplicationStatus.APPLIED)
                ));

        mockMvc.perform(get("/api/applications/1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Google"));
    }


    // TEST 4: APPLICATION NOT FOUND - 404
    @Test
    void shouldReturn404WhenApplicationDoesNotExist() throws Exception {

        when(jobApplicationService.getApplicationById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/applications/99"))

                .andExpect(status().isNotFound());
    }


    // TEST 5: UPDATE APPLICATION - 200 OK
    @Test
    void shouldUpdateApplication() throws Exception {

        JobApplicationResponse response =
                createResponse(1L, ApplicationStatus.INTERVIEW);

        when(jobApplicationService.updateJobApplication(
                eq(1L),
                any(com.sneha.jobapplicationtracker.dto.JobApplicationRequest.class)
        )).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/applications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("Interview"));
    }


    // TEST 6: DELETE APPLICATION - 204 NO CONTENT
    @Test
    void shouldDeleteApplication() throws Exception {

        when(jobApplicationService.deleteApplication(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/applications/1"))

                .andExpect(status().isNoContent());

        verify(jobApplicationService).deleteApplication(1L);
    }


    // TEST 7: DELETE MISSING APPLICATION - 404
    @Test
    void shouldReturn404WhenDeletingMissingApplication()
            throws Exception {

        when(jobApplicationService.deleteApplication(99L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/applications/99"))

                .andExpect(status().isNotFound());
    }


    // TEST 8: SEARCH APPLICATIONS - 200 OK
    @Test
    void shouldSearchApplications() throws Exception {

        when(jobApplicationService.searchApplicationsByCompanyName("Google"))
                .thenReturn(List.of(
                        createResponse(1L, ApplicationStatus.APPLIED)
                ));

        mockMvc.perform(get("/api/applications/search")
                        .param("companyName", "Google"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Google"));
    }


    // TEST 9: FILTER APPLICATIONS - 200 OK
    @Test
    void shouldFilterApplicationsByStatus() throws Exception {

        when(jobApplicationService.filterApplicationsByStatus(
                ApplicationStatus.INTERVIEW
        )).thenReturn(List.of(
                createResponse(1L, ApplicationStatus.INTERVIEW)
        ));

        mockMvc.perform(get("/api/applications/filter")
                        .param("status", "Interview"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Interview"));
    }


    // TEST 10: DASHBOARD STATISTICS - 200 OK
    @Test
    void shouldGetDashboardStatistics() throws Exception {

        Map<String, Long> statistics = Map.of(
                "totalApplications", 4L,
                "applied", 2L,
                "interviews", 2L,
                "offers", 0L,
                "rejected", 0L,
                "withdrawn", 0L
        );

        when(jobApplicationService.getDashboardStatistics())
                .thenReturn(statistics);

        mockMvc.perform(get("/api/applications/dashboard/stats"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(4))
                .andExpect(jsonPath("$.applied").value(2))
                .andExpect(jsonPath("$.interviews").value(2));
    }


    // TEST 11: PAGINATION - 200 OK
    @Test
    void shouldGetPaginatedApplications() throws Exception {

        JobApplicationResponse response =
                createResponse(1L, ApplicationStatus.APPLIED);

        PageResponse<JobApplicationResponse> pageResponse =
                new PageResponse<>(
                        new PageImpl<>(
                                List.of(response),
                                PageRequest.of(0, 5),
                                1
                        )
                );

        when(jobApplicationService.getApplicationsPage(
                any(Pageable.class)
        )).thenReturn(pageResponse);

        mockMvc.perform(get("/api/applications/page")
                        .param("page", "0")
                        .param("size", "5"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].companyName")
                        .value("Google"));
    }


    // TEST 12: INVALID PAGE SIZE - 400 BAD REQUEST
    @Test
    void shouldRejectInvalidPageSize() throws Exception {

        mockMvc.perform(get("/api/applications/page")
                        .param("page", "0")
                        .param("size", "150"))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Page size must be between 1 and 100"));
    }


    // TEST 13: INVALID SORT FIELD - 400 BAD REQUEST
    @Test
    void shouldRejectInvalidSortField() throws Exception {

        mockMvc.perform(get("/api/applications/page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "unknown"))

                .andExpect(status().isBadRequest());
    }


    // HELPER: CREATE RESPONSE DTO
    private JobApplicationResponse createResponse(
            Long id, ApplicationStatus status) {

        return new JobApplicationResponse(
                id,
                "Google",
                "Java Developer",
                "Bangalore",
                "Full-time",
                status,
                LocalDate.now(),
                "https://careers.google.com",
                new BigDecimal("75000"),
                "Test application"
        );
    }


    // HELPER: CREATE VALID JSON REQUEST
    private String validRequestJson() {

        return """
                {
                    "companyName": "Google",
                    "jobTitle": "Java Developer",
                    "location": "Bangalore",
                    "jobType": "Full-time",
                    "status": "Applied",
                    "appliedDate": "%s",
                    "jobUrl": "https://careers.google.com",
                    "salary": 75000,
                    "notes": "Test application"
                }
                """.formatted(LocalDate.now());
    }
}
