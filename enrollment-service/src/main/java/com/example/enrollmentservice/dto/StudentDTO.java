package com.example.enrollmentservice.dto;

public record StudentDTO(
        Long id,
        String cnie,
        String firstName,
        String lastName,
        String email
) {
}
