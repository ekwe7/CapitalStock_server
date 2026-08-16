package com.ekwe_hub.capitalstock_server.catalog.dto.request;

public record UpdateCategoryRequest(
    String categoryName,
    String categoryDescription
) {}
