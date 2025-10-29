package com.vendeskgt.vendeskgt.domain.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record TenantDto(
        UUID tenantId,
        String name,
        String lastName,
        String schemaName,
        String contactEmail,
        Timestamp createdAt,
        Boolean isActive
) {
}
