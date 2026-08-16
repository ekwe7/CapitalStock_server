package com.ekwe_hub.capitalstock_server.inventory.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
    UUID categoryId,
    String barcode,
    String name,
    String sku,
    BigDecimal costPrice,
    BigDecimal sellingPrice,
    Integer initialStockQuantity,
    BigDecimal advanceRatePercentage
) {}
