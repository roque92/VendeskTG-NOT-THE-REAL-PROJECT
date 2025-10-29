package com.vendeskgt.vendeskgt.mapper;

import com.vendeskgt.vendeskgt.domain.dto.TenantDto;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    //Request to Entity
    @Mapping(target = "createdAt", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "isActive", constant = "false")
    @Mapping(target = "tenantId", ignore = true)
    TenantEntity toEntity(TenantWithSubscriptionRegistrationRequest request);

    //Entity to Response
    TenantRegistrationResponse toResponse(TenantEntity customer);

    //Entity to DTO General
    TenantDto toDto(TenantEntity customer);

}
