package com.example.enrollmentservice.mapper;

import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.dto.DashboardCourseDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;
import com.example.enrollmentservice.dto.StudentDTO;
import com.example.enrollmentservice.dto.StudentDashboardDTO;
import com.example.enrollmentservice.entity.Enrollment;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class EnrollmentMapper {

    private EnrollmentMapper() {
    }

    public static Enrollment toEntity(Long studentId, Long courseId) {
        return new Enrollment(studentId, courseId);
    }

    public static EnrollmentResponseDTO toResponse(Enrollment enrollment) {
        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getEnrolledAt()
        );
    }

    public static DashboardCourseDTO toDashboardCourse(Enrollment enrollment, CourseDTO course, boolean canCancel) {
        return new DashboardCourseDTO(
                enrollment.getId(),
                enrollment.getCourseId(),
                course.title(),
                enrollment.getEnrolledAt(),
                canCancel
        );
    }

    public static StudentDashboardDTO toDashboard(StudentDTO student, List<DashboardCourseDTO> courses) {
        return new StudentDashboardDTO(
                student.id(),
                student.cnie(),
                student.firstName(),
                student.lastName(),
                List.copyOf(courses)
        );
    }

    public static StudentDashboardDTO toDashboard(
            StudentDTO student,
            List<Enrollment> enrollments,
            Function<Long, CourseDTO> courseResolver,
            Predicate<Enrollment> cancellationRule
    ) {
        List<DashboardCourseDTO> courses = enrollments.stream()
                .map(enrollment -> toDashboardCourse(
                        enrollment,
                        courseResolver.apply(enrollment.getCourseId()),
                        cancellationRule.test(enrollment)
                ))
                .toList();
        return toDashboard(student, courses);
    }
}
