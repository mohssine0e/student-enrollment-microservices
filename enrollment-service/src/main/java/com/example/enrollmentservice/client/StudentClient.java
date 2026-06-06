package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StudentClient {

    private final WebClient webClient;

    public StudentClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.student.base-url}") String studentServiceBaseUrl
    ) {
        this.webClient = webClientBuilder.clone()
                .baseUrl(studentServiceBaseUrl)
                .build();
    }

    public StudentDTO findByCnie(String cnie) {
        return webClient.get()
                .uri("/students/cnie/{cnie}", cnie)
                .retrieve()
                .bodyToMono(StudentDTO.class)
                .block();
    }
}
