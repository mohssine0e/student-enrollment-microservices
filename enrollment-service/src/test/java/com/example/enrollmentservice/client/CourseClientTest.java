package com.example.enrollmentservice.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.exception.CourseNotFoundException;
import com.example.enrollmentservice.exception.CourseServiceUnavailableException;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class CourseClientTest {

    private HttpServer server;
    private String requestedPath;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/courses/20", exchange -> {
            requestedPath = exchange.getRequestURI().getPath();
            byte[] response = """
                    {
                      "id": 20,
                      "title": "Distributed Systems",
                      "description": "Microservices course"
                    }
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream body = exchange.getResponseBody()) {
                body.write(response);
            }
        });
        server.createContext("/courses/999", exchange -> {
            requestedPath = exchange.getRequestURI().getPath();
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        server.createContext("/courses/404", exchange -> {
            requestedPath = exchange.getRequestURI().getPath();
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void findByIdFetchesCourseFromCourseService() {
        CourseClient client = new CourseClient(WebClient.builder(), baseUrl());

        CourseDTO course = client.findById(20L);

        assertThat(requestedPath).isEqualTo("/courses/20");
        assertThat(course.id()).isEqualTo(20L);
        assertThat(course.title()).isEqualTo("Distributed Systems");
        assertThat(course.description()).isEqualTo("Microservices course");
    }

    @Test
    void findByIdThrowsWhenCourseServiceReturnsServerError() {
        CourseClient client = new CourseClient(WebClient.builder(), baseUrl());

        assertThatThrownBy(() -> client.findById(999L))
                .isInstanceOf(CourseServiceUnavailableException.class)
                .hasMessage("Course Service is unavailable");
        assertThat(requestedPath).isEqualTo("/courses/999");
    }

    @Test
    void findByIdThrowsWhenCourseDoesNotExist() {
        CourseClient client = new CourseClient(WebClient.builder(), baseUrl());

        assertThatThrownBy(() -> client.findById(404L))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found with id: 404");
        assertThat(requestedPath).isEqualTo("/courses/404");
    }

    private String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }
}
