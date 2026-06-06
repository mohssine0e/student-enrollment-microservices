package com.example.enrollmentservice.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.enrollmentservice.dto.CourseDTO;
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

    private String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }
}
