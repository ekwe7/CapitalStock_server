package com.ekwe_hub.capitalstock_server.inventory.repository;

import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByMerchantId(UUID merchantId);
    Optional<Product> findByMerchantIdAndBarcode(UUID merchantId, String barcode);
    boolean existsByMerchantIdAndBarcode(UUID merchantId, String barcode);
    boolean existsByMerchantIdAndSku(UUID merchantId, String sku);

    List<Product> findByMerchantIdAndCategoryId(UUID merchantId, UUID categoryId);
    List<Product> findByMerchantIdAndVerificationStatus(UUID merchantId, Product.StockVerificationStatus verificationStatus);
    List<Product> findByMerchantIdAndAvailableQuantityLessThanEqual(UUID merchantId, Integer threshold);
}
