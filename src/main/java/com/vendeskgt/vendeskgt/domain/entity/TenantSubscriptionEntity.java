package com.vendeskgt.vendeskgt.domain.entity;

import com.vendeskgt.vendeskgt.domain.enums.PaymentMethodEnum;
import com.vendeskgt.vendeskgt.domain.enums.PlanTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "tenant_subscription_information")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenantEntity;

    @Column(name = "plan_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanTypeEnum planType;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Column(name = "subscription_status", nullable = false)
    @Builder.Default
    private Boolean subscriptionStatus = true;

    @CreationTimestamp
    @Column(name = "start_date", nullable = false, updatable = false, insertable = false)
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;
}
