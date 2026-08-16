package com.ekwe_hub.capitalstock_server.catalog.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    UUID merchantId,
    String categoryName,
    String categoryDescription,
    LocalDateTime createdAt
) {}
