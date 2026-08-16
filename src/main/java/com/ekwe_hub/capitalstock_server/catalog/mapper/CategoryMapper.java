package com.ekwe_hub.capitalstock_server.catalog.mapper;

import com.ekwe_hub.capitalstock_server.catalog.dto.request.CreateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.response.CategoryResponse;
import com.ekwe_hub.capitalstock_server.catalog.model.Category;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequest request, UUID merchantId) {
        if (request == null) return null;
        return Category.builder()
                .merchantId(merchantId)
                .categoryName(request.categoryName())
                .categoryDescription(request.categoryDescription())
                .build();
    }

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;
        return new CategoryResponse(
                entity.getId(),
                entity.getMerchantId(),
                entity.getCategoryName(),
                entity.getCategoryDescription(),
                entity.getCreatedAt()
        );
    }
}
