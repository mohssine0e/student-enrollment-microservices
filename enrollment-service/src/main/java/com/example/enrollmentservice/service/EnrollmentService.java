package com.example.enrollmentservice.service;

import com.example.enrollmentservice.dto.EnrollmentRequestDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;
import com.example.enrollmentservice.entity.Enrollment;

public interface EnrollmentService {

    EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO request);

    void ensureCourseHasCapacity(Long courseId);

    Enrollment findEnrollmentForDeletion(Long enrollmentId);
}
