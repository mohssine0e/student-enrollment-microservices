package com.example.enrollmentservice.repository;

import com.example.enrollmentservice.entity.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    long countByCourseId(Long courseId);

    List<Enrollment> findByStudentId(Long studentId);
}
