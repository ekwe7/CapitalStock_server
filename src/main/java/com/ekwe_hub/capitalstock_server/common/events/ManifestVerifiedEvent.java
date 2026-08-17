package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManifestVerifiedEvent(
    UUID manifestId,
    UUID merchantId,
    UUID supplierId,
    String manifestNumber,
    int totalVerifiedQuantity,
    UUID verifiedByUserId,
    LocalDateTime timestamp
) {}
