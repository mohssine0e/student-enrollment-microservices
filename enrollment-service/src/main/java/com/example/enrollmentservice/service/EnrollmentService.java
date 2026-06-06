package com.example.enrollmentservice.service;

import com.example.enrollmentservice.dto.EnrollmentRequestDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;

public interface EnrollmentService {

    EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO request);

    void ensureCourseHasCapacity(Long courseId);
}
