package com.vendeskgt.vendeskgt.domain.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public record TenantRegistrationResponse(
        UUID tenantId,
        String schemaName,
        String name,
        String lastName,
        String contactEmail,
        // Datos de suscripción
        String planType,
        String paymentMethod,
        Timestamp subscriptionStartDate,
        Timestamp subscriptionEndDate
) {
}
