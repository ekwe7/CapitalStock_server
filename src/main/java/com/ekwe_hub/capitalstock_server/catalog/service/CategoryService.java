package com.ekwe_hub.capitalstock_server.catalog.service;

import com.ekwe_hub.capitalstock_server.catalog.dto.request.CreateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.request.UpdateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createProductCategoryForMerchant(UUID merchantId, CreateCategoryRequest request);
    CategoryResponse updateProductCategoryForMerchant(UUID merchantId, UUID categoryId, UpdateCategoryRequest request);
    List<CategoryResponse> retrieveAllProductCategoriesForMerchant(UUID merchantId);
    CategoryResponse retrieveProductCategoryById(UUID merchantId, UUID categoryId);
}
