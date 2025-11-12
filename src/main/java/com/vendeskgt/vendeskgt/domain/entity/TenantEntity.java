package com.vendeskgt.vendeskgt.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@Entity
@Table(name = "tenant_information", uniqueConstraints = @UniqueConstraint(columnNames = {"schema_name"}))
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name =  "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "schema_name", nullable = false)
    private String schemaName;

    @Column(name = "contact_email", nullable = false, unique = true)
    private String contactEmail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = false;

    @OneToMany(mappedBy = "tenantEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TenantSubscriptionEntity> subscriptions = new ArrayList<>();

}
