package com.ekwe_hub.capitalstock_server.procurement.repository;

import com.ekwe_hub.capitalstock_server.procurement.model.SupplierInvoiceManifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierInvoiceManifestRepository extends JpaRepository<SupplierInvoiceManifest, UUID> {
    List<SupplierInvoiceManifest> findByMerchantId(UUID merchantId);
    List<SupplierInvoiceManifest> findBySupplierId(UUID supplierId);
    Optional<SupplierInvoiceManifest> findByManifestNumber(String manifestNumber);
    boolean existsByManifestNumber(String manifestNumber);
}
