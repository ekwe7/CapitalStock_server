package com.ekwe_hub.capitalstock_server.inventory.service.impl;

import com.ekwe_hub.capitalstock_server.common.events.ProductCreatedEvent;
import com.ekwe_hub.capitalstock_server.common.events.ProductUpdatedEvent;
import com.ekwe_hub.capitalstock_server.common.events.StockAdjustedEvent;
import com.ekwe_hub.capitalstock_server.common.events.StockMutationEvent;
import com.ekwe_hub.capitalstock_server.inventory.dto.request.*;
import com.ekwe_hub.capitalstock_server.inventory.dto.response.*;
import com.ekwe_hub.capitalstock_server.inventory.mapper.ProductMapper;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import com.ekwe_hub.capitalstock_server.inventory.repository.ProductRepository;
import com.ekwe_hub.capitalstock_server.inventory.service.ProductCatalogService;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCatalogServiceImpl implements ProductCatalogService {

    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ProductResponse createProductInMerchantCatalog(UUID merchantId, CreateProductRequest request, UUID createdByUserId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new IllegalArgumentException("Merchant business record not found for ID: " + merchantId);
        }

        if (productRepository.existsByMerchantIdAndBarcode(merchantId, request.barcode())) {
            throw new IllegalArgumentException("Product barcode already exists for this merchant: " + request.barcode());
        }

        if (request.sku() != null && !request.sku().isBlank() && productRepository.existsByMerchantIdAndSku(merchantId, request.sku())) {
            throw new IllegalArgumentException("Product SKU already exists for this merchant: " + request.sku());
        }

        Product product = productMapper.toEntity(request, merchantId);
        // Baseline initial products are strictly UNVERIFIED_MANUAL
        product.setVerificationStatus(Product.StockVerificationStatus.UNVERIFIED_MANUAL);

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishEvent(new ProductCreatedEvent(
                savedProduct.getId(),
                savedProduct.getMerchantId(),
                savedProduct.getBarcode(),
                savedProduct.getSku(),
                savedProduct.getName(),
                savedProduct.getVerificationStatus().name(),
                createdByUserId,
                LocalDateTime.now()
        ));

        if (savedProduct.getAvailableQuantity() > 0) {
            eventPublisher.publishEvent(new StockMutationEvent(
                    savedProduct.getMerchantId(),
                    savedProduct.getId(),
                    "INITIAL_UNVERIFIED_STOCK_ADDITION",
                    savedProduct.getAvailableQuantity(),
                    savedProduct.getAvailableQuantity()
            ));
        }

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProductDetailsInMerchantCatalog(UUID merchantId, UUID productId, UpdateProductRequest request, UUID updatedByUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product record not found for ID: " + productId));

        if (!product.getMerchantId().equals(merchantId)) {
            throw new IllegalArgumentException("Product does not belong to specified merchant ID");
        }

        if (request.categoryId() != null) {
            product.setCategoryId(request.categoryId());
        }
        if (request.name() != null && !request.name().isBlank()) {
            product.setName(request.name());
        }
        if (request.sku() != null) {
            product.setSku(request.sku());
        }
        if (request.costPrice() != null) {
            product.setCostPrice(request.costPrice());
        }
        if (request.sellingPrice() != null) {
            product.setSellingPrice(request.sellingPrice());
            product.setPrice(request.sellingPrice());
        }
        if (request.advanceRatePercentage() != null) {
            product.setAdvanceRatePercentage(request.advanceRatePercentage());
        }

        Product updatedProduct = productRepository.save(product);

        eventPublisher.publishEvent(new ProductUpdatedEvent(
                updatedProduct.getId(),
                updatedProduct.getMerchantId(),
                updatedProduct.getName(),
                updatedProduct.getSku(),
                updatedByUserId,
                LocalDateTime.now()
        ));

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse performManualUnverifiedStockAdjustment(UUID merchantId, UUID productId, PerformManualStockAdjustmentRequest request, UUID adjustedByUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product record not found for ID: " + productId));

        if (!product.getMerchantId().equals(merchantId)) {
            throw new IllegalArgumentException("Product does not belong to specified merchant ID");
        }

        int previousQuantity = product.getAvailableQuantity();
        int newQuantity = previousQuantity + request.stockAdjustmentQuantityDelta();

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Manual stock adjustment cannot result in negative stock quantity: " + newQuantity);
        }

        product.setAvailableQuantity(newQuantity);
        // Manual adjustments remain strictly UNVERIFIED_MANUAL baseline
        product.setVerificationStatus(Product.StockVerificationStatus.UNVERIFIED_MANUAL);

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishEvent(new StockAdjustedEvent(
                savedProduct.getId(),
                savedProduct.getMerchantId(),
                previousQuantity,
                request.stockAdjustmentQuantityDelta(),
                newQuantity,
                savedProduct.getVerificationStatus().name(),
                request.adjustmentReason(),
                adjustedByUserId,
                LocalDateTime.now()
        ));

        eventPublisher.publishEvent(new StockMutationEvent(
                savedProduct.getMerchantId(),
                savedProduct.getId(),
                "MANUAL_UNVERIFIED_ADJUSTMENT",
                request.stockAdjustmentQuantityDelta(),
                newQuantity
        ));

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProductByBarcodeForMerchant(UUID merchantId, String barcode) {
        Product product = productRepository.findByMerchantIdAndBarcode(merchantId, barcode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with barcode: " + barcode + " for merchant: " + merchantId));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> retrieveAllProductsForMerchant(UUID merchantId) {
        return productRepository.findByMerchantId(merchantId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> retrieveProductsByMerchantAndCategory(UUID merchantId, UUID categoryId) {
        return productRepository.findByMerchantIdAndCategoryId(merchantId, categoryId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> retrieveProductsByMerchantAndVerificationStatus(UUID merchantId, Product.StockVerificationStatus verificationStatus) {
        return productRepository.findByMerchantIdAndVerificationStatus(merchantId, verificationStatus)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> retrieveLowStockAlertProductsForMerchant(UUID merchantId, Integer threshold) {
        int stockThreshold = threshold != null ? threshold : 5;
        return productRepository.findByMerchantIdAndAvailableQuantityLessThanEqual(merchantId, stockThreshold)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
