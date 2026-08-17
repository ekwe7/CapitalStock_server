package com.ekwe_hub.capitalstock_server.ledger.repository;

import com.ekwe_hub.capitalstock_server.ledger.model.AuditBreachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditBreachAlertRepository extends JpaRepository<AuditBreachAlert, UUID> {
    List<AuditBreachAlert> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
    List<AuditBreachAlert> findByMerchantIdAndResolved(UUID merchantId, Boolean resolved);
    List<AuditBreachAlert> findByResolvedFalse();
}
