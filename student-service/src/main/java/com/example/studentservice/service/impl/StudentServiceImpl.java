package com.example.studentservice.service.impl;

import com.example.studentservice.dto.StudentRequestDTO;
import com.example.studentservice.dto.StudentResponseDTO;
import com.example.studentservice.entity.Student;
import com.example.studentservice.exception.StudentNotFoundException;
import com.example.studentservice.mapper.StudentMapper;
import com.example.studentservice.repository.StudentRepository;
import com.example.studentservice.service.StudentService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        if (studentRepository.existsByCnie(request.cnie())) {
            throw new IllegalArgumentException("Student CNIE already exists: " + request.cnie());
        }

        Student student = StudentMapper.toEntity(request);
        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        return StudentMapper.toResponse(findStudentById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByCnie(String cnie) {
        Student student = studentRepository.findByCnie(cnie)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with CNIE: " + cnie));
        return StudentMapper.toResponse(student);
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
        Student student = findStudentById(id);
        StudentMapper.updateEntity(student, request);
        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toResponse(savedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = findStudentById(id);
        studentRepository.delete(student);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
    }
}
