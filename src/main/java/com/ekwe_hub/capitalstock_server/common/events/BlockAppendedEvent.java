package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BlockAppendedEvent(
    UUID ledgerBlockId,
    UUID merchantId,
    long sequenceNumber,
    UUID productId,
    String eventType,
    String currentSignatureHash,
    LocalDateTime timestamp
) {}
