package com.ekwe_hub.capitalstock_server.procurement.dto.response;

import com.ekwe_hub.capitalstock_server.procurement.model.SupplierInvoiceManifest;

import java.util.UUID;

public record ManifestCheckinProgressResponse(
    UUID manifestId,
    String manifestNumber,
    SupplierInvoiceManifest.ManifestStatus status,
    int totalExpectedItemsCount,
    int totalScannedItemsCount,
    double checkinCompletionPercentageProgress
) {}
