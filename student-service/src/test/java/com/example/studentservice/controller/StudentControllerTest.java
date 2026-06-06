package com.example.studentservice.controller;

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

import com.example.studentservice.dto.StudentRequestDTO;
import com.example.studentservice.dto.StudentResponseDTO;
import com.example.studentservice.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void createStudentReturnsCreatedStudent() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO("AB123456", "Sara", "Amrani", "sara@example.com");
        StudentResponseDTO response = new StudentResponseDTO(1L, "AB123456", "Sara", "Amrani", "sara@example.com");
        when(studentService.createStudent(any(StudentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnie").value("AB123456"));
    }

    @Test
    void getAllStudentsReturnsStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(
                new StudentResponseDTO(1L, "AB123456", "Sara", "Amrani", "sara@example.com")
        ));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cnie").value("AB123456"));
    }

    @Test
    void getStudentByIdReturnsStudent() throws Exception {
        when(studentService.getStudentById(1L))
                .thenReturn(new StudentResponseDTO(1L, "AB123456", "Sara", "Amrani", "sara@example.com"));

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getStudentByCnieReturnsStudent() throws Exception {
        when(studentService.getStudentByCnie("AB123456"))
                .thenReturn(new StudentResponseDTO(1L, "AB123456", "Sara", "Amrani", "sara@example.com"));

        mockMvc.perform(get("/students/cnie/AB123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnie").value("AB123456"));
    }

    @Test
    void updateStudentReturnsUpdatedStudent() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO("AB123456", "Sara", "Amrani", "sara@example.com");
        when(studentService.updateStudent(any(Long.class), any(StudentRequestDTO.class)))
                .thenReturn(new StudentResponseDTO(1L, "AB123456", "Sara", "Amrani", "sara@example.com"));

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteStudentReturnsNoContent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createStudentWithInvalidBodyReturnsBadRequest() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO("", "", "", "not-an-email");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
