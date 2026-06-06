package com.example.enrollmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EnrollmentRequestDTO(
        @NotBlank(message = "CNIE is required")
        @Size(max = 32, message = "CNIE must be at most 32 characters")
        String cnie,

        @NotNull(message = "Course id is required")
        @Positive(message = "Course id must be positive")
        Long courseId
) {
}
