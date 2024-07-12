package com.compass.application.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "Name is required")
        String username,
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "Email is required")
        String email
) {
}
