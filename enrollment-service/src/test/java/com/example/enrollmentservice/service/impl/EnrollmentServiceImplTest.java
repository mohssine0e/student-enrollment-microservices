package com.example.enrollmentservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.enrollmentservice.client.CourseServiceClient;
import com.example.enrollmentservice.client.StudentServiceClient;
import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.dto.EnrollmentRequestDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;
import com.example.enrollmentservice.dto.StudentDTO;
import com.example.enrollmentservice.entity.Enrollment;
import com.example.enrollmentservice.exception.CourseFullException;
import com.example.enrollmentservice.exception.StudentNotFoundException;
import com.example.enrollmentservice.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentServiceClient studentClient;

    @Mock
    private CourseServiceClient courseClient;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    void ensureCourseHasCapacityAllowsCourseWithFewerThanThreeEnrollments() {
        when(enrollmentRepository.countByCourseId(20L)).thenReturn(2L);

        enrollmentService.ensureCourseHasCapacity(20L);

        verify(enrollmentRepository).countByCourseId(20L);
    }

    @Test
    void ensureCourseHasCapacityRejectsCourseWithThreeEnrollments() {
        when(enrollmentRepository.countByCourseId(20L)).thenReturn(3L);

        assertThatThrownBy(() -> enrollmentService.ensureCourseHasCapacity(20L))
                .isInstanceOf(CourseFullException.class)
                .hasMessage("Course has reached the maximum number of enrollments");
    }

    @Test
    void createEnrollmentUsesCnieAndCourseIdThenStoresOnlyForeignKeys() {
        LocalDateTime enrolledAt = LocalDateTime.of(2026, 6, 6, 10, 30);
        EnrollmentRequestDTO request = new EnrollmentRequestDTO("AA123456", 20L);
        when(studentClient.findByCnie("AA123456"))
                .thenReturn(new StudentDTO(10L, "AA123456", "Sara", "Amrani", "sara@example.com"));
        when(courseClient.findById(20L))
                .thenReturn(new CourseDTO(20L, "Distributed Systems", "Microservices course"));
        when(enrollmentRepository.countByCourseId(20L)).thenReturn(2L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment enrollment = invocation.getArgument(0);
            enrollment.setEnrolledAt(enrolledAt);
            return enrollment;
        });

        EnrollmentResponseDTO response = enrollmentService.createEnrollment(request);

        assertThat(response.studentId()).isEqualTo(10L);
        assertThat(response.courseId()).isEqualTo(20L);
        assertThat(response.enrolledAt()).isEqualTo(enrolledAt);
        verify(studentClient).findByCnie("AA123456");
        verify(courseClient).findById(20L);
        verify(enrollmentRepository).countByCourseId(20L);
        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(enrollmentCaptor.capture());
        assertThat(enrollmentCaptor.getValue().getStudentId()).isEqualTo(10L);
        assertThat(enrollmentCaptor.getValue().getCourseId()).isEqualTo(20L);
    }

    @Test
    void createEnrollmentStopsWhenStudentDoesNotExist() {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO("MISSING", 20L);
        when(studentClient.findByCnie("MISSING"))
                .thenThrow(new StudentNotFoundException("Student not found with CNIE: MISSING"));

        assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found with CNIE: MISSING");
        verify(courseClient, never()).findById(any());
        verify(enrollmentRepository, never()).save(any());
    }
}
