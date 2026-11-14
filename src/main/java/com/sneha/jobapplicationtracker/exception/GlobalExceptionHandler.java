
package com.sneha.jobapplicationtracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // HANDLE VALIDATION ERRORS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return ResponseEntity.badRequest().body(errors);
    }

    // HANDLE INVALID JSON, DATE AND ENUM VALUES
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestBody(
            HttpMessageNotReadableException exception) {

        Map<String, String> error = new LinkedHashMap<>();

        error.put(
                "error",
                "Invalid request body. Check your JSON format, date format, and status values."
        );

        return ResponseEntity.badRequest().body(error);
    }

    // HANDLE MISSING REQUEST PARAMETERS
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameters(
            MissingServletRequestParameterException exception) {

        Map<String, String> error = new LinkedHashMap<>();

        error.put(
                "error",
                "Required parameter is missing: " + exception.getParameterName()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // HANDLE INVALID PARAMETER TYPES
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        Map<String, String> error = new LinkedHashMap<>();

        error.put(
                "error",
                "Invalid value provided for parameter: " + exception.getName()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // HANDLE INVALID STATUS OR OTHER ILLEGAL ARGUMENTS
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception) {

        Map<String, String> error = new LinkedHashMap<>();

        error.put("error", exception.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    // HANDLE UNEXPECTED ERRORS
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(
            Exception exception) {

        logger.error("Unexpected application error", exception);

        Map<String, String> error = new LinkedHashMap<>();

        error.put(
                "error",
                "An unexpected server error occurred. Please try again later."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
