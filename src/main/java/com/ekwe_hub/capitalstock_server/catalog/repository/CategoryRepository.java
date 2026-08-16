package com.ekwe_hub.capitalstock_server.catalog.repository;

import com.ekwe_hub.capitalstock_server.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByMerchantId(UUID merchantId);
    Optional<Category> findByMerchantIdAndCategoryName(UUID merchantId, String categoryName);
    boolean existsByMerchantIdAndCategoryName(UUID merchantId, String categoryName);
}
