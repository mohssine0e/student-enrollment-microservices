package com.example.enrollmentservice.dto;

import java.time.LocalDateTime;

public record EnrollmentResponseDTO(
        Long id,
        Long studentId,
        Long courseId,
        LocalDateTime enrolledAt
) {
}
