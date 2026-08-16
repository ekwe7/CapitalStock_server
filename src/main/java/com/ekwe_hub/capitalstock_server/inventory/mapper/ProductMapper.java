package com.ekwe_hub.capitalstock_server.inventory.mapper;

import com.ekwe_hub.capitalstock_server.inventory.dto.request.CreateProductRequest;
import com.ekwe_hub.capitalstock_server.inventory.dto.response.ProductResponse;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request, UUID merchantId) {
        if (request == null) return null;
        BigDecimal selling = request.sellingPrice() != null ? request.sellingPrice() : BigDecimal.ZERO;
        BigDecimal cost = request.costPrice() != null ? request.costPrice() : BigDecimal.ZERO;
        int initialQty = request.initialStockQuantity() != null ? request.initialStockQuantity() : 0;
        BigDecimal advanceRate = request.advanceRatePercentage() != null ? request.advanceRatePercentage() : BigDecimal.ZERO;

        return Product.builder()
                .merchantId(merchantId)
                .categoryId(request.categoryId())
                .barcode(request.barcode())
                .name(request.name())
                .sku(request.sku())
                .costPrice(cost)
                .sellingPrice(selling)
                .price(selling)
                .availableQuantity(initialQty)
                .reservedQuantity(0)
                .advanceRatePercentage(advanceRate)
                .verificationStatus(Product.StockVerificationStatus.UNVERIFIED_MANUAL) // Baseline initial products are strictly unverified
                .build();
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) return null;
        return new ProductResponse(
                entity.getId(),
                entity.getMerchantId(),
                entity.getCategoryId(),
                entity.getBarcode(),
                entity.getName(),
                entity.getSku(),
                entity.getCostPrice(),
                entity.getSellingPrice(),
                entity.getPrice(),
                entity.getAvailableQuantity(),
                entity.getReservedQuantity(),
                entity.getAdvanceRatePercentage(),
                entity.getVerificationStatus(),
                entity.getCreatedAt()
        );
    }
}
