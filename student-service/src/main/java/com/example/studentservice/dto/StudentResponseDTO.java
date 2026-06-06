package com.example.studentservice.dto;

public record StudentResponseDTO(
        Long id,
        String cnie,
        String firstName,
        String lastName,
        String email
) {
}
