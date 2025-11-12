package com.vendeskgt.vendeskgt.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TenantWithSubscriptionRegistrationRequest(
        @NotBlank(message = "Nombre es requerido")
        String name,
        @NotBlank(message = "Apellido es requerido")
        String lastName,
        @NotBlank(message = "Nombre de la empresa es requerido")
        String schemaName,
        @NotBlank(message = "Email es requerido")
        @Email
        String contactEmail,
        @NotBlank(message = "Password es requerido")
        @Size(min = 10, max = 15, message = "Password debe tener entre 10 y 15 caracteres")
        String password,
        @NotNull(message = "Información de subscripción es requerida")
        @Valid
        TenantSubscriptionData subscription
) {
}
