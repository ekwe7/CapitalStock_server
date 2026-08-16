package com.ekwe_hub.capitalstock_server.inventory.service;

import com.ekwe_hub.capitalstock_server.inventory.dto.request.*;
import com.ekwe_hub.capitalstock_server.inventory.dto.response.*;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductCatalogService {
    ProductResponse createProductInMerchantCatalog(UUID merchantId, CreateProductRequest request, UUID createdByUserId);
    ProductResponse updateProductDetailsInMerchantCatalog(UUID merchantId, UUID productId, UpdateProductRequest request, UUID updatedByUserId);
    ProductResponse performManualUnverifiedStockAdjustment(UUID merchantId, UUID productId, PerformManualStockAdjustmentRequest request, UUID adjustedByUserId);
    ProductResponse findProductByBarcodeForMerchant(UUID merchantId, String barcode);
    List<ProductResponse> retrieveAllProductsForMerchant(UUID merchantId);
    List<ProductResponse> retrieveProductsByMerchantAndCategory(UUID merchantId, UUID categoryId);
    List<ProductResponse> retrieveProductsByMerchantAndVerificationStatus(UUID merchantId, Product.StockVerificationStatus verificationStatus);
    List<ProductResponse> retrieveLowStockAlertProductsForMerchant(UUID merchantId, Integer threshold);
}
