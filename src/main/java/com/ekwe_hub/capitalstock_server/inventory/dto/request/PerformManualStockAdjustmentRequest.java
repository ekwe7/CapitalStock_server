package com.ekwe_hub.capitalstock_server.inventory.dto.request;

public record PerformManualStockAdjustmentRequest(
    int stockAdjustmentQuantityDelta,
    String adjustmentReason
) {}
