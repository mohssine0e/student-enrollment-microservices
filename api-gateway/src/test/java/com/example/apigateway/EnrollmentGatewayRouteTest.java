package com.example.apigateway;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class EnrollmentGatewayRouteTest {

    private static final HttpServer ENROLLMENT_SERVER = startEnrollmentServer();

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        registry.add("ENROLLMENT_SERVICE_URL", () -> "http://localhost:" + ENROLLMENT_SERVER.getAddress().getPort());
    }

    @AfterAll
    static void tearDown() {
        ENROLLMENT_SERVER.stop(0);
    }

    @Test
    void forwardsEnrollmentsPathToEnrollmentService() {
        webTestClient.get()
                .uri("/enrollments/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("enrollment-route-ok");
    }

    private static HttpServer startEnrollmentServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
            server.createContext("/enrollments/ping", exchange -> {
                byte[] response = "enrollment-route-ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream body = exchange.getResponseBody()) {
                    body.write(response);
                }
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to start mock Enrollment Service", exception);
        }
    }
}
