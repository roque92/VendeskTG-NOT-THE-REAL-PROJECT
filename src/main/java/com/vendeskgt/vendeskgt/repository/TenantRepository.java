package com.vendeskgt.vendeskgt.repository;

import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

    List<TenantEntity> findBySchemaNameStartingWithIgnoreCase(String prefix);
    Optional<TenantEntity> findByContactEmail(String email);

}
