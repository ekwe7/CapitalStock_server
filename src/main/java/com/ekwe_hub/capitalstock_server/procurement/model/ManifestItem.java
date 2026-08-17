package com.ekwe_hub.capitalstock_server.procurement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "manifest_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManifestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "manifest_id", nullable = false)
    private UUID manifestId;

    @Column(nullable = false)
    private String barcode;

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "scanned_quantity", nullable = false)
    @Builder.Default
    private Integer scannedQuantity = 0;
}
