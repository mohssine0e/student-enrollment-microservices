package com.example.enrollmentservice.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.enrollmentservice.dto.StudentDTO;
import com.example.enrollmentservice.exception.StudentServiceUnavailableException;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class StudentClientTest {

    private HttpServer server;
    private String requestedPath;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/students/cnie/AA123456", exchange -> {
            requestedPath = exchange.getRequestURI().getPath();
            byte[] response = """
                    {
                      "id": 10,
                      "cnie": "AA123456",
                      "firstName": "Sara",
                      "lastName": "Amrani",
                      "email": "sara@example.com"
                    }
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream body = exchange.getResponseBody()) {
                body.write(response);
            }
        });
        server.createContext("/students/cnie/SERVICE_DOWN", exchange -> {
            requestedPath = exchange.getRequestURI().getPath();
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void findByCnieFetchesStudentFromStudentService() {
        StudentClient client = new StudentClient(WebClient.builder(), baseUrl());

        StudentDTO student = client.findByCnie("AA123456");

        assertThat(requestedPath).isEqualTo("/students/cnie/AA123456");
        assertThat(student.id()).isEqualTo(10L);
        assertThat(student.cnie()).isEqualTo("AA123456");
        assertThat(student.firstName()).isEqualTo("Sara");
        assertThat(student.lastName()).isEqualTo("Amrani");
        assertThat(student.email()).isEqualTo("sara@example.com");
    }

    @Test
    void findByCnieThrowsWhenStudentServiceReturnsServerError() {
        StudentClient client = new StudentClient(WebClient.builder(), baseUrl());

        assertThatThrownBy(() -> client.findByCnie("SERVICE_DOWN"))
                .isInstanceOf(StudentServiceUnavailableException.class)
                .hasMessage("Student Service is unavailable");
        assertThat(requestedPath).isEqualTo("/students/cnie/SERVICE_DOWN");
    }

    private String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }
}
