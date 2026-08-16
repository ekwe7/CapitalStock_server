package com.ekwe_hub.capitalstock_server.admin.dto.response;

import java.math.BigDecimal;

public record PlatformMetricsResponse(
    BigDecimal grossMerchandiseValue, // Total transaction GMV across all channels
    BigDecimal platformRevenue,        // Estimated fee split revenue
    long totalOrdersProcessed,
    long totalVerifiedProductsInStock,
    long totalVettedSuppliers
) {}
