package com.ekwe_hub.capitalstock_server.catalog.service.impl;

import com.ekwe_hub.capitalstock_server.catalog.dto.request.CreateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.request.UpdateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.response.CategoryResponse;
import com.ekwe_hub.capitalstock_server.catalog.mapper.CategoryMapper;
import com.ekwe_hub.capitalstock_server.catalog.model.Category;
import com.ekwe_hub.capitalstock_server.catalog.repository.CategoryRepository;
import com.ekwe_hub.capitalstock_server.catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createProductCategoryForMerchant(UUID merchantId, CreateCategoryRequest request) {
        if (categoryRepository.existsByMerchantIdAndCategoryName(merchantId, request.categoryName())) {
            throw new IllegalArgumentException("Category name already exists for this merchant: " + request.categoryName());
        }

        Category category = categoryMapper.toEntity(request, merchantId);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateProductCategoryForMerchant(UUID merchantId, UUID categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        if (!category.getMerchantId().equals(merchantId)) {
            throw new IllegalArgumentException("Category does not belong to specified merchant ID");
        }

        if (request.categoryName() != null && !request.categoryName().isBlank()) {
            category.setCategoryName(request.categoryName());
        }
        if (request.categoryDescription() != null) {
            category.setCategoryDescription(request.categoryDescription());
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> retrieveAllProductCategoriesForMerchant(UUID merchantId) {
        return categoryRepository.findByMerchantId(merchantId)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse retrieveProductCategoryById(UUID merchantId, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        if (!category.getMerchantId().equals(merchantId)) {
            throw new IllegalArgumentException("Category does not belong to specified merchant ID");
        }

        return categoryMapper.toResponse(category);
    }
}
