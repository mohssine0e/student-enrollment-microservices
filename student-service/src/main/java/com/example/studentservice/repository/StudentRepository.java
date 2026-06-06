package com.example.studentservice.repository;

import com.example.studentservice.entity.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByCnie(String cnie);

    boolean existsByCnie(String cnie);
}
