package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.CourseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CourseClient {

    private final WebClient webClient;

    public CourseClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.course.base-url}") String courseServiceBaseUrl
    ) {
        this.webClient = webClientBuilder.clone()
                .baseUrl(courseServiceBaseUrl)
                .build();
    }

    public CourseDTO findById(Long courseId) {
        return webClient.get()
                .uri("/courses/{id}", courseId)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();
    }
}
