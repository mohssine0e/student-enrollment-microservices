package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.CourseDTO;
import com.example.enrollmentservice.exception.CourseNotFoundException;
import com.example.enrollmentservice.exception.CourseServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Service
public class CourseClient implements CourseServiceClient {

    private final WebClient webClient;

    public CourseClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.course.base-url}") String courseServiceBaseUrl
    ) {
        this.webClient = webClientBuilder.clone()
                .baseUrl(courseServiceBaseUrl)
                .build();
    }

    @Override
    public CourseDTO findById(Long courseId) {
        try {
            return webClient.get()
                    .uri("/courses/{id}", courseId)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, response -> response.releaseBody()
                            .then(Mono.error(new CourseNotFoundException("Course not found with id: " + courseId))))
                    .onStatus(HttpStatusCode::is5xxServerError, response -> response.releaseBody()
                            .then(Mono.error(new CourseServiceUnavailableException("Course Service is unavailable"))))
                    .bodyToMono(CourseDTO.class)
                    .block();
        } catch (WebClientRequestException exception) {
            throw new CourseServiceUnavailableException("Course Service is unavailable", exception);
        }
    }
}
