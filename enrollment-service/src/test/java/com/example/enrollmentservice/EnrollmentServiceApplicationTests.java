package com.example.enrollmentservice;

import com.example.enrollmentservice.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
class EnrollmentServiceApplicationTests {

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @Test
    void contextLoads() {
    }
}
