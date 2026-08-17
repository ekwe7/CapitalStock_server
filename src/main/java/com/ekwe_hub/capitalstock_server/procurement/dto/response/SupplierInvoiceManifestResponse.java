package com.ekwe_hub.capitalstock_server.procurement.dto.response;

import com.ekwe_hub.capitalstock_server.procurement.model.SupplierInvoiceManifest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SupplierInvoiceManifestResponse(
    UUID id,
    UUID merchantId,
    UUID supplierId,
    String manifestNumber,
    SupplierInvoiceManifest.ManifestStatus status,
    LocalDateTime createdAt,
    LocalDateTime reconciledAt,
    List<ManifestItemResponse> manifestItems
) {}
