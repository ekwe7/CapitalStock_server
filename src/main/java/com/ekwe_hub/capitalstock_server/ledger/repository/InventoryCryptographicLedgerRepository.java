package com.ekwe_hub.capitalstock_server.ledger.repository;

import com.ekwe_hub.capitalstock_server.ledger.model.InventoryCryptographicLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryCryptographicLedgerRepository extends JpaRepository<InventoryCryptographicLedger, UUID> {
    List<InventoryCryptographicLedger> findByMerchantIdOrderBySequenceNumberAsc(UUID merchantId);
    Optional<InventoryCryptographicLedger> findTopByMerchantIdOrderBySequenceNumberDesc(UUID merchantId);
    Optional<InventoryCryptographicLedger> findByMerchantIdAndSequenceNumber(UUID merchantId, Long sequenceNumber);
    long countByMerchantId(UUID merchantId);
}
