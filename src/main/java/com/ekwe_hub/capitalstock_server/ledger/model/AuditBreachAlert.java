package com.ekwe_hub.capitalstock_server.ledger.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_breach_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditBreachAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "sequence_number")
    private Long sequenceNumber;

    @Column(name = "alert_type", nullable = false)
    private String alertType; // e.g. "TAMPER_DETECTED", "HASH_MISMATCH", "UNVERIFIED_QUANTITY"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean resolved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
