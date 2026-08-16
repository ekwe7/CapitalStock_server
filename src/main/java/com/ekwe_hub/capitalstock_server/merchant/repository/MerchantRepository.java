package com.ekwe_hub.capitalstock_server.merchant.repository;

import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    Optional<Merchant> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByStatus(Merchant.MerchantStatus status);
}
