package com.example.courseservice.service.impl;

import com.example.courseservice.dto.CourseRequestDTO;
import com.example.courseservice.dto.CourseResponseDTO;
import com.example.courseservice.entity.Course;
import com.example.courseservice.exception.CourseNotFoundException;
import com.example.courseservice.mapper.CourseMapper;
import com.example.courseservice.repository.CourseRepository;
import com.example.courseservice.service.CourseService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        Course course = CourseMapper.toEntity(request);
        Course savedCourse = courseRepository.save(course);
        return CourseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        return CourseMapper.toResponse(findCourseById(id));
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request) {
        Course course = findCourseById(id);
        CourseMapper.updateEntity(course, request);
        Course savedCourse = courseRepository.save(course);
        return CourseMapper.toResponse(savedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courseRepository.delete(course);
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));
    }
}
