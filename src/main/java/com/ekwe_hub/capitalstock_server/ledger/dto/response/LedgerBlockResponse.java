package com.ekwe_hub.capitalstock_server.ledger.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerBlockResponse(
    UUID id,
    UUID merchantId,
    long sequenceNumber,
    UUID productId,
    String eventType,
    int quantityChange,
    int resultingBalance,
    String recordPayloadHash,
    String previousHash,
    String currentSignatureHash,
    LocalDateTime createdAt
) {}
