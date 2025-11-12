package com.vendeskgt.vendeskgt.mapper;

import com.vendeskgt.vendeskgt.domain.dto.TenantDto;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import com.vendeskgt.vendeskgt.domain.entity.TenantSubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    // Mapea solo lo que existe en TenantEntity
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    TenantEntity toEntity(TenantWithSubscriptionRegistrationRequest request);

    @Mapping(target = "tenantId", source = "entity.tenantId")
    @Mapping(target = "schemaName", source = "entity.schemaName")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "lastName", source = "entity.lastName")
    @Mapping(target = "contactEmail", source = "entity.contactEmail")
    @Mapping(target = "planType", source = "subscription.planType")
    @Mapping(target = "paymentMethod", source = "subscription.paymentMethod")
    @Mapping(target = "subscriptionStartDate", source = "subscription.startDate")
    @Mapping(target = "subscriptionEndDate", source = "subscription.endDate")
    TenantRegistrationResponse toResponse(TenantEntity entity, TenantSubscriptionEntity subscription);

    TenantDto toDto(TenantEntity entity);
}