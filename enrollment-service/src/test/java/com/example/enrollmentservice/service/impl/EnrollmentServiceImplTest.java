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
import com.example.enrollmentservice.exception.CancellationPeriodExpiredException;
import com.example.enrollmentservice.exception.CourseFullException;
import com.example.enrollmentservice.exception.CourseNotFoundException;
import com.example.enrollmentservice.exception.StudentNotFoundException;
import com.example.enrollmentservice.repository.EnrollmentRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    private EnrollmentServiceImpl enrollmentService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2026, 6, 6, 12, 0).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );
        enrollmentService = new EnrollmentServiceImpl(enrollmentRepository, studentClient, courseClient, fixedClock);
    }

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

    @Test
    void createEnrollmentStopsWhenCourseDoesNotExist() {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO("AA123456", 404L);
        when(studentClient.findByCnie("AA123456"))
                .thenReturn(new StudentDTO(10L, "AA123456", "Sara", "Amrani", "sara@example.com"));
        when(courseClient.findById(404L))
                .thenThrow(new CourseNotFoundException("Course not found with id: 404"));

        assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found with id: 404");
        verify(enrollmentRepository, never()).countByCourseId(any());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void findEnrollmentForDeletionReturnsExistingEnrollment() {
        Enrollment enrollment = new Enrollment(10L, 20L);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment foundEnrollment = enrollmentService.findEnrollmentForDeletion(1L);

        assertThat(foundEnrollment).isSameAs(enrollment);
        verify(enrollmentRepository).findById(1L);
    }

    @Test
    void findEnrollmentForDeletionThrowsWhenEnrollmentDoesNotExist() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.findEnrollmentForDeletion(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Enrollment not found with id: 1");
    }

    @Test
    void cancelEnrollmentDeletesEnrollmentWithinTwentyFourHours() {
        Enrollment enrollment = new Enrollment(10L, 20L);
        enrollment.setEnrolledAt(LocalDateTime.of(2026, 6, 5, 13, 0));
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(1L);

        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void cancelEnrollmentRejectsEnrollmentAfterTwentyFourHours() {
        Enrollment enrollment = new Enrollment(10L, 20L);
        enrollment.setEnrolledAt(LocalDateTime.of(2026, 6, 5, 11, 59));
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment(1L))
                .isInstanceOf(CancellationPeriodExpiredException.class)
                .hasMessage("Enrollment cancellation period has expired");
        verify(enrollmentRepository, never()).delete(any());
    }
}
