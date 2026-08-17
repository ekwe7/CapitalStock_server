package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManifestDiscrepancyFlaggedEvent(
    UUID manifestId,
    UUID merchantId,
    UUID supplierId,
    String manifestNumber,
    String discrepancyReasonDetails,
    UUID flaggedByUserId,
    LocalDateTime timestamp
) {}
