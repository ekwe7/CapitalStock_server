package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerTamperDetectedEvent(
    UUID alertId,
    UUID merchantId,
    UUID productId,
    long corruptedSequenceNumber,
    String expectedHashSignature,
    String actualCorruptedHashSignature,
    String tamperDescription,
    LocalDateTime timestamp
) {}
