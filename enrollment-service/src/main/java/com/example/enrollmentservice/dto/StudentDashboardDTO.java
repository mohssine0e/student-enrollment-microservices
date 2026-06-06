package com.example.enrollmentservice.dto;

public record StudentDashboardDTO(
        Long studentId,
        String cnie,
        String firstName,
        String lastName
) {
}
