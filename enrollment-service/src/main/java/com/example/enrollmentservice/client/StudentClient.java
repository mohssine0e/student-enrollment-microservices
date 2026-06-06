package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.StudentDTO;
import com.example.enrollmentservice.exception.StudentServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

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
        try {
            return webClient.get()
                    .uri("/students/cnie/{cnie}", cnie)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, response -> response.releaseBody()
                            .then(Mono.error(new StudentServiceUnavailableException("Student Service is unavailable"))))
                    .bodyToMono(StudentDTO.class)
                    .block();
        } catch (WebClientRequestException exception) {
            throw new StudentServiceUnavailableException("Student Service is unavailable", exception);
        }
    }
}
