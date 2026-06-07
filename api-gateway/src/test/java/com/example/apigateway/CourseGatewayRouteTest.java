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
class CourseGatewayRouteTest {

    private static final HttpServer COURSE_SERVER = startCourseServer();

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        registry.add("COURSE_SERVICE_URL", () -> "http://localhost:" + COURSE_SERVER.getAddress().getPort());
    }

    @AfterAll
    static void tearDown() {
        COURSE_SERVER.stop(0);
    }

    @Test
    void forwardsCoursesPathToCourseService() {
        webTestClient.get()
                .uri("/courses/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("course-route-ok");
    }

    private static HttpServer startCourseServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
            server.createContext("/courses/ping", exchange -> {
                byte[] response = "course-route-ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream body = exchange.getResponseBody()) {
                    body.write(response);
                }
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to start mock Course Service", exception);
        }
    }
}
