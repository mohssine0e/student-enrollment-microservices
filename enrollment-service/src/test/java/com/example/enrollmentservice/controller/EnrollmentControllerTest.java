package com.example.enrollmentservice.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.enrollmentservice.dto.DashboardCourseDTO;
import com.example.enrollmentservice.dto.EnrollmentRequestDTO;
import com.example.enrollmentservice.dto.EnrollmentResponseDTO;
import com.example.enrollmentservice.dto.StudentDashboardDTO;
import com.example.enrollmentservice.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    void createEnrollmentReturnsCreatedEnrollment() throws Exception {
        LocalDateTime enrolledAt = LocalDateTime.of(2026, 6, 6, 10, 30);
        EnrollmentRequestDTO request = new EnrollmentRequestDTO("AA123456", 20L);
        when(enrollmentService.createEnrollment(request))
                .thenReturn(new EnrollmentResponseDTO(1L, 10L, 20L, enrolledAt));

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(10))
                .andExpect(jsonPath("$.courseId").value(20));
    }

    @Test
    void createEnrollmentValidatesRequest() throws Exception {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO("", -1L);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void cancelEnrollmentReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/enrollments/1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService).cancelEnrollment(1L);
    }

    @Test
    void getDashboardReturnsDashboardResponse() throws Exception {
        StudentDashboardDTO dashboard = new StudentDashboardDTO(
                10L,
                "AA123456",
                "Sara",
                "Amrani",
                List.of(new DashboardCourseDTO(
                        1L,
                        20L,
                        "Distributed Systems",
                        LocalDateTime.of(2026, 6, 6, 10, 30),
                        true
                ))
        );
        when(enrollmentService.getDashboardByCnie("AA123456")).thenReturn(dashboard);

        mockMvc.perform(get("/enrollments/dashboard/AA123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(10))
                .andExpect(jsonPath("$.cnie").value("AA123456"))
                .andExpect(jsonPath("$.firstName").value("Sara"))
                .andExpect(jsonPath("$.lastName").value("Amrani"))
                .andExpect(jsonPath("$.courses[0].enrollmentId").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value(20))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Distributed Systems"))
                .andExpect(jsonPath("$.courses[0].enrolledAt").exists())
                .andExpect(jsonPath("$.courses[0].canCancel").value(true));
    }
}
