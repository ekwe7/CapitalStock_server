package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockAdjustedEvent(
    UUID productId,
    UUID merchantId,
    int previousStockQuantity,
    int adjustedQuantityDelta,
    int newAvailableStockQuantity,
    String stockVerificationStatus, // Strictly "UNVERIFIED_MANUAL" for manual adjustments
    String adjustmentReason,
    UUID adjustedByUserId,
    LocalDateTime timestamp
) {}
