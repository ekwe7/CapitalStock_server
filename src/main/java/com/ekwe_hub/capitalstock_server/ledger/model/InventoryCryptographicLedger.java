package com.ekwe_hub.capitalstock_server.ledger.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_cryptographic_ledger", uniqueConstraints = {
    @UniqueConstraint(name = "uq_merchant_sequence", columnNames = {"merchant_id", "sequence_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCryptographicLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "sequence_number", nullable = false)
    private Long sequenceNumber;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Column(name = "resulting_balance", nullable = false)
    private Integer resultingBalance;

    @Embedded
    private HashProof hashProof;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
