package com.ekwe_hub.capitalstock_server.ledger.dto.request;

import java.util.UUID;

public record AppendStockMutationLedgerBlockRequest(
    UUID merchantId,
    UUID productId,
    String eventType,
    int quantityChange,
    int resultingBalance
) {}
