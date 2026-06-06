package com.example.courseservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.courseservice.dto.CourseRequestDTO;
import com.example.courseservice.dto.CourseResponseDTO;
import com.example.courseservice.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    void createCourseReturnsCreatedCourse() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO("Spring Boot", "Build services with Spring Boot.");
        CourseResponseDTO response = new CourseResponseDTO(1L, "Spring Boot", "Build services with Spring Boot.");
        when(courseService.createCourse(any(CourseRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot"));
    }

    @Test
    void getAllCoursesReturnsCourses() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of(
                new CourseResponseDTO(1L, "Spring Boot", "Build services with Spring Boot.")
        ));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Spring Boot"));
    }

    @Test
    void getCourseByIdReturnsCourse() throws Exception {
        when(courseService.getCourseById(1L))
                .thenReturn(new CourseResponseDTO(1L, "Spring Boot", "Build services with Spring Boot."));

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateCourseReturnsUpdatedCourse() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO("Spring Boot", "Updated course description.");
        when(courseService.updateCourse(any(Long.class), any(CourseRequestDTO.class)))
                .thenReturn(new CourseResponseDTO(1L, "Spring Boot", "Updated course description."));

        mockMvc.perform(put("/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated course description."));
    }

    @Test
    void deleteCourseReturnsNoContent() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createCourseWithInvalidBodyReturnsBadRequest() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO("", "");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
