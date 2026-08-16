package com.ekwe_hub.capitalstock_server.inventory.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
    UUID categoryId,
    String name,
    String sku,
    BigDecimal costPrice,
    BigDecimal sellingPrice,
    BigDecimal advanceRatePercentage
) {}
