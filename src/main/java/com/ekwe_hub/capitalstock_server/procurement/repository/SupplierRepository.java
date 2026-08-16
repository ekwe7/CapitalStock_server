package com.ekwe_hub.capitalstock_server.procurement.repository;

import com.ekwe_hub.capitalstock_server.procurement.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Optional<Supplier> findByCode(String code);
    boolean existsByCode(String code);
    long countByStatus(Supplier.SupplierStatus status);
}
