package com.ekwe_hub.capitalstock_server.inventory.controller;

import com.ekwe_hub.capitalstock_server.inventory.dto.request.*;
import com.ekwe_hub.capitalstock_server.inventory.dto.response.*;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import com.ekwe_hub.capitalstock_server.inventory.service.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;

    @PostMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<ProductResponse> createProductInMerchantCatalog(
            @PathVariable UUID merchantId,
            @RequestBody CreateProductRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productCatalogService.createProductInMerchantCatalog(merchantId, request, null));
    }

    @PutMapping("/{productId}/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<ProductResponse> updateProductDetailsInMerchantCatalog(
            @PathVariable UUID merchantId,
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(productCatalogService.updateProductDetailsInMerchantCatalog(merchantId, productId, request, null));
    }

    @PostMapping("/{productId}/merchant/{merchantId}/stock-adjustment")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<ProductResponse> performManualUnverifiedStockAdjustment(
            @PathVariable UUID merchantId,
            @PathVariable UUID productId,
            @RequestBody PerformManualStockAdjustmentRequest request
    ) {
        return ResponseEntity.ok(productCatalogService.performManualUnverifiedStockAdjustment(merchantId, productId, request, null));
    }

    @GetMapping("/merchant/{merchantId}/barcode/{barcode}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<ProductResponse> findProductByBarcodeForMerchant(
            @PathVariable UUID merchantId,
            @PathVariable String barcode
    ) {
        return ResponseEntity.ok(productCatalogService.findProductByBarcodeForMerchant(merchantId, barcode));
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<ProductResponse>> retrieveAllProductsForMerchant(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(productCatalogService.retrieveAllProductsForMerchant(merchantId));
    }

    @GetMapping("/merchant/{merchantId}/category/{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<ProductResponse>> retrieveProductsByMerchantAndCategory(
            @PathVariable UUID merchantId,
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(productCatalogService.retrieveProductsByMerchantAndCategory(merchantId, categoryId));
    }

    @GetMapping("/merchant/{merchantId}/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<ProductResponse>> retrieveProductsByMerchantAndVerificationStatus(
            @PathVariable UUID merchantId,
            @PathVariable Product.StockVerificationStatus status
    ) {
        return ResponseEntity.ok(productCatalogService.retrieveProductsByMerchantAndVerificationStatus(merchantId, status));
    }

    @GetMapping("/merchant/{merchantId}/low-stock")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<ProductResponse>> retrieveLowStockAlertProductsForMerchant(
            @PathVariable UUID merchantId,
            @RequestParam(required = false, defaultValue = "5") Integer threshold
    ) {
        return ResponseEntity.ok(productCatalogService.retrieveLowStockAlertProductsForMerchant(merchantId, threshold));
    }
}
