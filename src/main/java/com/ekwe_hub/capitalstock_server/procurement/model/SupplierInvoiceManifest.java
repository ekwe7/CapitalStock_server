package com.ekwe_hub.capitalstock_server.procurement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "supplier_invoice_manifests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierInvoiceManifest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "manifest_number", nullable = false, unique = true)
    private String manifestNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ManifestStatus status = ManifestStatus.PENDING_CHECKIN;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reconciled_at")
    private LocalDateTime reconciledAt;

    @OneToMany(mappedBy = "manifestId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ManifestItem> manifestItems = new ArrayList<>();

    public enum ManifestStatus {
        PENDING_CHECKIN,
        IN_PROGRESS,
        RECONCILED,
        DISCREPANCY_REJECTED
    }
}
