package com.example.studentservice.dto;

public record StudentRequestDTO(
        String cnie,
        String firstName,
        String lastName,
        String email
) {
}
