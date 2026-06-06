package com.example.studentservice;

import com.example.studentservice.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
class StudentServiceApplicationTests {

    @MockBean
    private StudentRepository studentRepository;

    @Test
    void contextLoads() {
    }
}
