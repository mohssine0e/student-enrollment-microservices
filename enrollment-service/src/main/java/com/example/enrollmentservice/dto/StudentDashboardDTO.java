package com.example.enrollmentservice.dto;

import java.util.List;

public record StudentDashboardDTO(
        Long studentId,
        String cnie,
        String firstName,
        String lastName,
        List<DashboardCourseDTO> courses
) {
}
