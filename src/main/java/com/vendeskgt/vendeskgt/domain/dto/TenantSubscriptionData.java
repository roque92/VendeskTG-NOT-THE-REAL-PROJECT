package com.vendeskgt.vendeskgt.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record TenantSubscriptionData(
        @NotBlank(message = "Plan type es requerido")
        String planType,
        @NotBlank(message = "Payment method es requerido")
        String paymentMethod
) {
}
