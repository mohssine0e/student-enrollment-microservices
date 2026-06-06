package com.example.enrollmentservice.repository;

import com.example.enrollmentservice.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    long countByCourseId(Long courseId);
}
