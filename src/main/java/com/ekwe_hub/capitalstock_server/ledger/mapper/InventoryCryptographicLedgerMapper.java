package com.ekwe_hub.capitalstock_server.ledger.mapper;

import com.ekwe_hub.capitalstock_server.ledger.dto.response.AuditBreachAlertResponse;
import com.ekwe_hub.capitalstock_server.ledger.dto.response.LedgerBlockResponse;
import com.ekwe_hub.capitalstock_server.ledger.model.AuditBreachAlert;
import com.ekwe_hub.capitalstock_server.ledger.model.InventoryCryptographicLedger;
import org.springframework.stereotype.Component;

@Component
public class InventoryCryptographicLedgerMapper {

    public LedgerBlockResponse toBlockResponse(InventoryCryptographicLedger entity) {
        if (entity == null) return null;
        String payloadHash = entity.getHashProof() != null ? entity.getHashProof().getRecordPayloadHash() : null;
        String prevHash = entity.getHashProof() != null ? entity.getHashProof().getPreviousHash() : null;
        String sigHash = entity.getHashProof() != null ? entity.getHashProof().getCurrentSignatureHash() : null;

        return new LedgerBlockResponse(
                entity.getId(),
                entity.getMerchantId(),
                entity.getSequenceNumber(),
                entity.getProductId(),
                entity.getEventType(),
                entity.getQuantityChange(),
                entity.getResultingBalance(),
                payloadHash,
                prevHash,
                sigHash,
                entity.getCreatedAt()
        );
    }

    public AuditBreachAlertResponse toAlertResponse(AuditBreachAlert entity) {
        if (entity == null) return null;
        return new AuditBreachAlertResponse(
                entity.getId(),
                entity.getMerchantId(),
                entity.getProductId(),
                entity.getSequenceNumber(),
                entity.getAlertType(),
                entity.getDescription(),
                entity.getResolved(),
                entity.getCreatedAt()
        );
    }
}
