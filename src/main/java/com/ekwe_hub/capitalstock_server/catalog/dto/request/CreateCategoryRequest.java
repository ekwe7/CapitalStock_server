package com.ekwe_hub.capitalstock_server.catalog.dto.request;

public record CreateCategoryRequest(
    String categoryName,
    String categoryDescription
) {}
