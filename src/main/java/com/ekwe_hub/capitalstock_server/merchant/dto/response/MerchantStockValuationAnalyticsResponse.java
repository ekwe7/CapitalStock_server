package com.ekwe_hub.capitalstock_server.merchant.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record MerchantStockValuationAnalyticsResponse(
    UUID merchantId,
    BigDecimal totalInventoryCostValuationAmount,
    BigDecimal totalPotentialSellingValuationAmount,
    long totalUniqueProductSkusCount,
    long totalAvailableStockItemsCount,
    long totalReservedStockItemsCount,
    long unverifiedManualStockItemsCount,
    long verifiedSupplierStockItemsCount
) {}
