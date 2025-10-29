package com.vendeskgt.vendeskgt.repository;

import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

    List<TenantEntity> findBySchemaNameStartingWithIgnoreCase(String prefix);
}
