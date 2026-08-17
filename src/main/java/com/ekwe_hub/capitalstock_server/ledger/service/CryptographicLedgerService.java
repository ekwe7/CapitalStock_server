package com.ekwe_hub.capitalstock_server.ledger.service;

import com.ekwe_hub.capitalstock_server.ledger.dto.request.AppendStockMutationLedgerBlockRequest;
import com.ekwe_hub.capitalstock_server.ledger.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface CryptographicLedgerService {
    LedgerBlockResponse appendInventoryMutationLedgerBlock(AppendStockMutationLedgerBlockRequest request);
    LedgerAuditVerificationResponse verifyMerchantLedgerCryptographicIntegrity(UUID merchantId);
    List<LedgerBlockResponse> retrieveLedgerBlocksForMerchant(UUID merchantId);
    List<AuditBreachAlertResponse> retrieveAuditBreachAlertsForMerchant(UUID merchantId);
}
