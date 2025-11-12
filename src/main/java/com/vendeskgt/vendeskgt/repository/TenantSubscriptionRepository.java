package com.vendeskgt.vendeskgt.repository;

import com.vendeskgt.vendeskgt.domain.entity.TenantSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscriptionEntity, UUID> {
}
