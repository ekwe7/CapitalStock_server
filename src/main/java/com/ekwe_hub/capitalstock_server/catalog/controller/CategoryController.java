package com.ekwe_hub.capitalstock_server.catalog.controller;

import com.ekwe_hub.capitalstock_server.catalog.dto.request.CreateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.request.UpdateCategoryRequest;
import com.ekwe_hub.capitalstock_server.catalog.dto.response.CategoryResponse;
import com.ekwe_hub.capitalstock_server.catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<CategoryResponse> createProductCategoryForMerchant(
            @PathVariable UUID merchantId,
            @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createProductCategoryForMerchant(merchantId, request));
    }

    @PutMapping("/{categoryId}/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<CategoryResponse> updateProductCategoryForMerchant(
            @PathVariable UUID merchantId,
            @PathVariable UUID categoryId,
            @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateProductCategoryForMerchant(merchantId, categoryId, request));
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<CategoryResponse>> retrieveAllProductCategoriesForMerchant(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(categoryService.retrieveAllProductCategoriesForMerchant(merchantId));
    }

    @GetMapping("/{categoryId}/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<CategoryResponse> retrieveProductCategoryById(
            @PathVariable UUID merchantId,
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(categoryService.retrieveProductCategoryById(merchantId, categoryId));
    }
}
