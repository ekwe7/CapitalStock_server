package com.ekwe_hub.capitalstock_server.inventory.dto.response;

import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    UUID merchantId,
    UUID categoryId,
    String barcode,
    String name,
    String sku,
    BigDecimal costPrice,
    BigDecimal sellingPrice,
    BigDecimal price,
    Integer availableQuantity,
    Integer reservedQuantity,
    BigDecimal advanceRatePercentage,
    Product.StockVerificationStatus verificationStatus,
    LocalDateTime createdAt
) {}
