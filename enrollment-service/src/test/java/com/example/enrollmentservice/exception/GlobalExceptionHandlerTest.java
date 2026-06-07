package com.example.enrollmentservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundReturnsNotFoundResponse() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new EnrollmentNotFoundException("Enrollment not found with id: 1"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Enrollment not found with id: 1");
    }

    @Test
    void handleConflictReturnsConflictResponse() {
        ResponseEntity<ErrorResponse> response =
                handler.handleConflict(new CourseFullException("Course has reached the maximum number of enrollments"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    void handleServiceUnavailableReturnsServiceUnavailableResponse() {
        ResponseEntity<ErrorResponse> response =
                handler.handleServiceUnavailable(new StudentServiceUnavailableException("Student Service is unavailable"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(503);
    }
}
