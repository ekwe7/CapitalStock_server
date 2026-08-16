package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductUpdatedEvent(
    UUID productId,
    UUID merchantId,
    String updatedProductName,
    String updatedSku,
    UUID updatedByUserId,
    LocalDateTime timestamp
) {}
