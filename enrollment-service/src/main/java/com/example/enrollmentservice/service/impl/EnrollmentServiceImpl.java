package com.example.enrollmentservice.service.impl;

import com.example.enrollmentservice.client.CourseServiceClient;
import com.example.enrollmentservice.client.StudentServiceClient;
import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.dto.EnrollmentRequestDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;
import com.example.enrollmentservice.dto.StudentDTO;
import com.example.enrollmentservice.entity.Enrollment;
import com.example.enrollmentservice.exception.CancellationPeriodExpiredException;
import com.example.enrollmentservice.exception.CourseFullException;
import com.example.enrollmentservice.exception.EnrollmentNotFoundException;
import com.example.enrollmentservice.mapper.EnrollmentMapper;
import com.example.enrollmentservice.repository.EnrollmentRepository;
import com.example.enrollmentservice.service.EnrollmentService;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final int MAX_ENROLLMENTS_PER_COURSE = 3;

    private final EnrollmentRepository enrollmentRepository;
    private final StudentServiceClient studentClient;
    private final CourseServiceClient courseClient;
    private final Clock clock;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentServiceClient studentClient,
            CourseServiceClient courseClient,
            Clock clock
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentClient = studentClient;
        this.courseClient = courseClient;
        this.clock = clock;
    }

    @Override
    public EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO request) {
        StudentDTO student = studentClient.findByCnie(request.cnie());
        CourseDTO course = courseClient.findById(request.courseId());
        ensureCourseHasCapacity(course.id());

        Enrollment enrollment = EnrollmentMapper.toEntity(student.id(), course.id());
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return EnrollmentMapper.toResponse(savedEnrollment);
    }

    @Override
    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = findEnrollmentForDeletion(enrollmentId);
        LocalDateTime cancellationDeadline = enrollment.getEnrolledAt().plusHours(24);
        if (LocalDateTime.now(clock).isAfter(cancellationDeadline)) {
            throw new CancellationPeriodExpiredException("Enrollment cancellation period has expired");
        }
        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public void ensureCourseHasCapacity(Long courseId) {
        long enrollmentCount = enrollmentRepository.countByCourseId(courseId);
        if (enrollmentCount >= MAX_ENROLLMENTS_PER_COURSE) {
            throw new CourseFullException("Course has reached the maximum number of enrollments");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Enrollment findEnrollmentForDeletion(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found with id: " + enrollmentId));
    }
}
