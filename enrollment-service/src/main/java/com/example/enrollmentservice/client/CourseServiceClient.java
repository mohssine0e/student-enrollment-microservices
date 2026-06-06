package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.CourseDTO;

public interface CourseServiceClient {

    CourseDTO findById(Long courseId);
}
