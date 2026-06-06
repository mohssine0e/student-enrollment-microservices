package com.example.courseservice.mapper;

import com.example.courseservice.dto.CourseRequestDTO;
import com.example.courseservice.dto.CourseResponseDTO;
import com.example.courseservice.entity.Course;

public final class CourseMapper {

    private CourseMapper() {
    }

    public static Course toEntity(CourseRequestDTO request) {
        return new Course(request.title(), request.description());
    }

    public static CourseResponseDTO toResponse(Course course) {
        return new CourseResponseDTO(course.getId(), course.getTitle(), course.getDescription());
    }

    public static void updateEntity(Course course, CourseRequestDTO request) {
        course.setTitle(request.title());
        course.setDescription(request.description());
    }
}
