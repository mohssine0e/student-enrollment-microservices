package com.example.enrollmentservice.service.impl;

import com.example.enrollmentservice.repository.EnrollmentRepository;
import com.example.enrollmentservice.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final int MAX_ENROLLMENTS_PER_COURSE = 3;

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void ensureCourseHasCapacity(Long courseId) {
        long enrollmentCount = enrollmentRepository.countByCourseId(courseId);
        if (enrollmentCount >= MAX_ENROLLMENTS_PER_COURSE) {
            throw new IllegalStateException("Course has reached the maximum number of enrollments");
        }
    }
}
