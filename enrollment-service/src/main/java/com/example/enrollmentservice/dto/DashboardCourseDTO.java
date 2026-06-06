package com.example.enrollmentservice.dto;

import java.time.LocalDateTime;

public record DashboardCourseDTO(
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        LocalDateTime enrolledAt,
        boolean canCancel
) {
}
