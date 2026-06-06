package com.example.enrollmentservice.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.enrollmentservice.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

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
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Course has reached the maximum number of enrollments");
    }
}
