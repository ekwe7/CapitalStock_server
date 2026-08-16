package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductCreatedEvent(
    UUID productId,
    UUID merchantId,
    String barcode,
    String sku,
    String productName,
    String stockVerificationStatus,
    UUID createdByUserId,
    LocalDateTime timestamp
) {}
