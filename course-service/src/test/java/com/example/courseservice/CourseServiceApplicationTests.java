package com.example.courseservice;

import com.example.courseservice.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
class CourseServiceApplicationTests {

    @MockBean
    private CourseRepository courseRepository;

    @Test
    void contextLoads() {
    }
}
