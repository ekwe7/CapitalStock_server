package com.ekwe_hub.capitalstock_server.common.events;

import java.util.UUID;

public record StockMutationEvent(
    UUID merchantId,
    UUID productId,
    String eventType,
    int quantityChange,
    int resultingBalance
) {}
