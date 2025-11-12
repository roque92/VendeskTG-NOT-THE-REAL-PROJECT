package com.vendeskgt.vendeskgt.domain.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TenantLoginRequestDTO(
        @Email
        @NotBlank(message = "Email es requerido")
        String email,
        @NotBlank(message = "Password es requerido")
        String password
) {
}
