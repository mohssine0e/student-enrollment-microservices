package com.example.enrollmentservice.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.dto.StudentDTO;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class WebClientIntegrationTest {

    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/students/cnie/AA123456", exchange -> writeJson(exchange, """
                {
                  "id": 10,
                  "cnie": "AA123456",
                  "firstName": "Sara",
                  "lastName": "Amrani",
                  "email": "sara@example.com"
                }
                """));
        server.createContext("/courses/20", exchange -> writeJson(exchange, """
                {
                  "id": 20,
                  "title": "Distributed Systems",
                  "description": "Microservices course"
                }
                """));
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void clientsFetchStudentAndCourseFromConfiguredBaseUrls() {
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        StudentClient studentClient = new StudentClient(WebClient.builder(), baseUrl);
        CourseClient courseClient = new CourseClient(WebClient.builder(), baseUrl);

        StudentDTO student = studentClient.findByCnie("AA123456");
        CourseDTO course = courseClient.findById(20L);

        assertThat(student.id()).isEqualTo(10L);
        assertThat(student.cnie()).isEqualTo("AA123456");
        assertThat(course.id()).isEqualTo(20L);
        assertThat(course.title()).isEqualTo("Distributed Systems");
    }

    private void writeJson(com.sun.net.httpserver.HttpExchange exchange, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream body = exchange.getResponseBody()) {
            body.write(response);
        }
    }
}
