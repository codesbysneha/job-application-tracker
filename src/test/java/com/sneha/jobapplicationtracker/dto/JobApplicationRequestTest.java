
package com.sneha.jobapplicationtracker.dto;

import com.sneha.jobapplicationtracker.enums.ApplicationStatus;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JobApplicationRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {

        validatorFactory = Validation.buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {

        validatorFactory.close();
    }


    // TEST 1: VALID REQUEST
    @Test
    void shouldAcceptValidRequest() {

        JobApplicationRequest request = createValidRequest();

        Set<ConstraintViolation<JobApplicationRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }


    // TEST 2: REQUIRED FIELDS
    @Test
    void shouldRejectMissingRequiredFields() {

        JobApplicationRequest request = new JobApplicationRequest();

        Set<ConstraintViolation<JobApplicationRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("companyName")
        ));

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("jobTitle")
        ));

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("status")
        ));

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("appliedDate")
        ));
    }


    // TEST 3: FUTURE APPLICATION DATE
    @Test
    void shouldRejectFutureAppliedDate() {

        JobApplicationRequest request = createValidRequest();

        request.setAppliedDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<JobApplicationRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("appliedDate")
        ));
    }


    // TEST 4: NEGATIVE SALARY
    @Test
    void shouldRejectNegativeSalary() {

        JobApplicationRequest request = createValidRequest();

        request.setSalary(new BigDecimal("-100"));

        Set<ConstraintViolation<JobApplicationRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("salary")
        ));
    }


    // TEST 5: INVALID JOB URL
    @Test
    void shouldRejectInvalidJobUrl() {

        JobApplicationRequest request = createValidRequest();

        request.setJobUrl("ftp://invalid-url.com");

        Set<ConstraintViolation<JobApplicationRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("jobUrl")
        ));
    }


    // TEST 6: COMPANY NAME TOO LONG
    @Test
    void shouldRejectCompanyNameExceedingMaximumLength() {

        JobApplicationRequest request = createValidRequest();

        request.setCompanyName("A".repeat(101));

        Set<ConstraintViolation<JobApplicationRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("companyName")
        ));
    }


    // HELPER: CREATE VALID REQUEST
    private JobApplicationRequest createValidRequest() {

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
}
