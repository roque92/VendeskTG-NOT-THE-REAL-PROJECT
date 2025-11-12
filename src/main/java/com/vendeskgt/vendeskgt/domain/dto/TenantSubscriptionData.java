package com.vendeskgt.vendeskgt.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TenantSubscriptionData(
        @NotBlank(message = "Plan type es requerido")
        @Pattern(regexp = "MONTHLY|ANNUAL", message = "PlanType inválido")
        String planType,
        @NotBlank(message = "Payment method es requerido")
        @Pattern(regexp = "CREDITCARD|TRANSFER", message = "PaymentMethod inválido")
        String paymentMethod
) {
}
