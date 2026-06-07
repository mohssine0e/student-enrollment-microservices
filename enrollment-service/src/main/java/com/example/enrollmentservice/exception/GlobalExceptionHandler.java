package com.example.enrollmentservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({StudentNotFoundException.class, CourseNotFoundException.class, EnrollmentNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler({CourseFullException.class, CancellationPeriodExpiredException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    @ExceptionHandler({StudentServiceUnavailableException.class, CourseServiceUnavailableException.class})
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(RuntimeException exception) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", Map.of());
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> validationErrors
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                validationErrors
        );
        return ResponseEntity.status(status).body(response);
    }
}
