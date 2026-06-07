package com.example.enrollmentservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.dto.DashboardCourseDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;
import com.example.enrollmentservice.dto.StudentDTO;
import com.example.enrollmentservice.dto.StudentDashboardDTO;
import com.example.enrollmentservice.entity.Enrollment;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class EnrollmentMapperTest {

    @Test
    void toEntityCreatesEnrollmentFromStudentAndCourseIds() {
        Enrollment enrollment = EnrollmentMapper.toEntity(10L, 20L);

        assertThat(enrollment.getStudentId()).isEqualTo(10L);
        assertThat(enrollment.getCourseId()).isEqualTo(20L);
    }

    @Test
    void toResponseMapsStoredEnrollmentFields() {
        LocalDateTime enrolledAt = LocalDateTime.of(2026, 6, 6, 10, 30);
        Enrollment enrollment = new Enrollment(10L, 20L);
        enrollment.setEnrolledAt(enrolledAt);

        EnrollmentResponseDTO response = EnrollmentMapper.toResponse(enrollment);

        assertThat(response.studentId()).isEqualTo(10L);
        assertThat(response.courseId()).isEqualTo(20L);
        assertThat(response.enrolledAt()).isEqualTo(enrolledAt);
    }

    @Test
    void toDashboardMapsStudentAndCourseEnrollmentInformation() {
        LocalDateTime enrolledAt = LocalDateTime.of(2026, 6, 6, 10, 30);
        Enrollment enrollment = new Enrollment(10L, 20L);
        enrollment.setEnrolledAt(enrolledAt);
        CourseDTO course = new CourseDTO(20L, "Distributed Systems", "Microservices course");
        StudentDTO student = new StudentDTO(10L, "AA123456", "Sara", "Amrani", "sara@example.com");

        DashboardCourseDTO courseRow = EnrollmentMapper.toDashboardCourse(enrollment, course, true);
        StudentDashboardDTO dashboard = EnrollmentMapper.toDashboard(student, List.of(courseRow));

        assertThat(courseRow.courseId()).isEqualTo(20L);
        assertThat(courseRow.courseTitle()).isEqualTo("Distributed Systems");
        assertThat(courseRow.enrolledAt()).isEqualTo(enrolledAt);
        assertThat(courseRow.canCancel()).isTrue();
        assertThat(dashboard.studentId()).isEqualTo(10L);
        assertThat(dashboard.cnie()).isEqualTo("AA123456");
        assertThat(dashboard.courses()).containsExactly(courseRow);
    }

    @Test
    void toDashboardMapsEnrollmentRowsWithResolvedCourseDetails() {
        LocalDateTime enrolledAt = LocalDateTime.of(2026, 6, 6, 10, 30);
        Enrollment enrollment = new Enrollment(10L, 20L);
        enrollment.setEnrolledAt(enrolledAt);
        StudentDTO student = new StudentDTO(10L, "AA123456", "Sara", "Amrani", "sara@example.com");

        StudentDashboardDTO dashboard = EnrollmentMapper.toDashboard(
                student,
                List.of(enrollment),
                courseId -> new CourseDTO(courseId, "Distributed Systems", "Microservices course"),
                ignored -> true
        );

        assertThat(dashboard.courses()).hasSize(1);
        assertThat(dashboard.courses().getFirst().courseId()).isEqualTo(20L);
        assertThat(dashboard.courses().getFirst().courseTitle()).isEqualTo("Distributed Systems");
        assertThat(dashboard.courses().getFirst().canCancel()).isTrue();
    }
}
