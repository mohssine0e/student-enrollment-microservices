package com.example.studentservice.mapper;

import com.example.studentservice.dto.StudentRequestDTO;
import com.example.studentservice.dto.StudentResponseDTO;
import com.example.studentservice.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentRequestDTO request) {
        return new Student(
                request.cnie(),
                request.firstName(),
                request.lastName(),
                request.email()
        );
    }

    public static StudentResponseDTO toResponse(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getCnie(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }

    public static void updateEntity(Student student, StudentRequestDTO request) {
        student.setCnie(request.cnie());
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
    }
}
