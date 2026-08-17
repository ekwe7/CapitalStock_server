package com.ekwe_hub.capitalstock_server.ledger.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditBreachAlertResponse(
    UUID id,
    UUID merchantId,
    UUID productId,
    Long sequenceNumber,
    String alertType,
    String description,
    Boolean resolved,
    LocalDateTime createdAt
) {}
