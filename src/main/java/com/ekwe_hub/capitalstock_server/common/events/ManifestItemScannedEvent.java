package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManifestItemScannedEvent(
    UUID manifestId,
    UUID merchantId,
    String scannedBarcode,
    int updatedScannedQuantity,
    int expectedQuantity,
    UUID scannedByUserId,
    LocalDateTime timestamp
) {}
