package com.ekwe_hub.capitalstock_server.ledger.service.impl;

import com.ekwe_hub.capitalstock_server.common.events.BlockAppendedEvent;
import com.ekwe_hub.capitalstock_server.common.events.LedgerTamperDetectedEvent;
import com.ekwe_hub.capitalstock_server.ledger.dto.request.AppendStockMutationLedgerBlockRequest;
import com.ekwe_hub.capitalstock_server.ledger.dto.response.*;
import com.ekwe_hub.capitalstock_server.ledger.mapper.InventoryCryptographicLedgerMapper;
import com.ekwe_hub.capitalstock_server.ledger.model.AuditBreachAlert;
import com.ekwe_hub.capitalstock_server.ledger.model.HashProof;
import com.ekwe_hub.capitalstock_server.ledger.model.InventoryCryptographicLedger;
import com.ekwe_hub.capitalstock_server.ledger.repository.AuditBreachAlertRepository;
import com.ekwe_hub.capitalstock_server.ledger.repository.InventoryCryptographicLedgerRepository;
import com.ekwe_hub.capitalstock_server.ledger.service.CryptographicLedgerService;
import com.ekwe_hub.capitalstock_server.ledger.util.Sha256CryptographicHashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CryptographicLedgerServiceImpl implements CryptographicLedgerService {

    private final InventoryCryptographicLedgerRepository ledgerRepository;
    private final AuditBreachAlertRepository auditBreachAlertRepository;
    private final Sha256CryptographicHashGenerator hashGenerator;
    private final InventoryCryptographicLedgerMapper ledgerMapper;
    private final ApplicationEventPublisher eventPublisher;

    private static final String GENESIS_PREVIOUS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

    @Override
    @Transactional
    public LedgerBlockResponse appendInventoryMutationLedgerBlock(AppendStockMutationLedgerBlockRequest request) {
        Optional<InventoryCryptographicLedger> latestBlockOpt = ledgerRepository.findTopByMerchantIdOrderBySequenceNumberDesc(request.merchantId());

        long nextSequenceNumber = latestBlockOpt.map(block -> block.getSequenceNumber() + 1).orElse(1L);
        String previousHash = latestBlockOpt.map(block -> block.getHashProof().getCurrentSignatureHash()).orElse(GENESIS_PREVIOUS_HASH);

        LocalDateTime now = LocalDateTime.now();
        String payloadHash = hashGenerator.computePayloadHash(
                nextSequenceNumber,
                request.merchantId(),
                request.productId(),
                request.eventType(),
                request.quantityChange(),
                request.resultingBalance(),
                now.toString()
        );

        String signatureHash = hashGenerator.computeSignatureHash(previousHash, payloadHash);

        HashProof proof = HashProof.builder()
                .recordPayloadHash(payloadHash)
                .previousHash(previousHash)
                .currentSignatureHash(signatureHash)
                .build();

        InventoryCryptographicLedger ledgerBlock = InventoryCryptographicLedger.builder()
                .merchantId(request.merchantId())
                .sequenceNumber(nextSequenceNumber)
                .productId(request.productId())
                .eventType(request.eventType())
                .quantityChange(request.quantityChange())
                .resultingBalance(request.resultingBalance())
                .hashProof(proof)
                .createdAt(now)
                .build();

        InventoryCryptographicLedger savedBlock = ledgerRepository.save(ledgerBlock);

        eventPublisher.publishEvent(new BlockAppendedEvent(
                savedBlock.getId(),
                savedBlock.getMerchantId(),
                savedBlock.getSequenceNumber(),
                savedBlock.getProductId(),
                savedBlock.getEventType(),
                savedBlock.getHashProof().getCurrentSignatureHash(),
                now
        ));

        return ledgerMapper.toBlockResponse(savedBlock);
    }

    @Override
    @Transactional
    public LedgerAuditVerificationResponse verifyMerchantLedgerCryptographicIntegrity(UUID merchantId) {
        List<InventoryCryptographicLedger> blocks = ledgerRepository.findByMerchantIdOrderBySequenceNumberAsc(merchantId);

        if (blocks.isEmpty()) {
            return new LedgerAuditVerificationResponse(
                    merchantId, true, 0, 0,
                    "No cryptographic ledger blocks exist for merchant.", LocalDateTime.now()
            );
        }

        String expectedPreviousHash = GENESIS_PREVIOUS_HASH;
        long corruptedCount = 0;

        for (InventoryCryptographicLedger block : blocks) {
            String actualPreviousHash = block.getHashProof().getPreviousHash();

            // Verify hash continuity from previous block
            if (!expectedPreviousHash.equals(actualPreviousHash)) {
                corruptedCount++;
                triggerTamperAlert(block, expectedPreviousHash, actualPreviousHash, "Previous hash continuity broken.");
            }

            // Recalculate payload and signature hash
            String recomputedPayloadHash = hashGenerator.computePayloadHash(
                    block.getSequenceNumber(),
                    block.getMerchantId(),
                    block.getProductId(),
                    block.getEventType(),
                    block.getQuantityChange(),
                    block.getResultingBalance(),
                    block.getCreatedAt().toString()
            );

            String recomputedSignatureHash = hashGenerator.computeSignatureHash(actualPreviousHash, recomputedPayloadHash);

            if (!recomputedSignatureHash.equals(block.getHashProof().getCurrentSignatureHash())) {
                corruptedCount++;
                triggerTamperAlert(block, recomputedSignatureHash, block.getHashProof().getCurrentSignatureHash(), "Payload or signature hash mismatch detected.");
            }

            expectedPreviousHash = block.getHashProof().getCurrentSignatureHash();
        }

        boolean isIntact = corruptedCount == 0;
        String summary = isIntact ?
                "Ledger integrity audit passed 100% cleanly. Hash chain continuity verified." :
                "TAMPER_DETECTED: Found " + corruptedCount + " corrupted or altered cryptographic ledger block(s).";

        return new LedgerAuditVerificationResponse(
                merchantId, isIntact, blocks.size(), corruptedCount, summary, LocalDateTime.now()
        );
    }

    private void triggerTamperAlert(InventoryCryptographicLedger block, String expectedHash, String actualHash, String reason) {
        AuditBreachAlert alert = AuditBreachAlert.builder()
                .merchantId(block.getMerchantId())
                .productId(block.getProductId())
                .sequenceNumber(block.getSequenceNumber())
                .alertType("TAMPER_DETECTED")
                .description("Cryptographic ledger breach at sequence #" + block.getSequenceNumber() + ". " + reason + " Expected: " + expectedHash + ", Found: " + actualHash)
                .resolved(false)
                .build();

        AuditBreachAlert savedAlert = auditBreachAlertRepository.save(alert);

        eventPublisher.publishEvent(new LedgerTamperDetectedEvent(
                savedAlert.getId(),
                block.getMerchantId(),
                block.getProductId(),
                block.getSequenceNumber(),
                expectedHash,
                actualHash,
                alert.getDescription(),
                LocalDateTime.now()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerBlockResponse> retrieveLedgerBlocksForMerchant(UUID merchantId) {
        return ledgerRepository.findByMerchantIdOrderBySequenceNumberAsc(merchantId)
                .stream()
                .map(ledgerMapper::toBlockResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditBreachAlertResponse> retrieveAuditBreachAlertsForMerchant(UUID merchantId) {
        return auditBreachAlertRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId)
                .stream()
                .map(ledgerMapper::toAlertResponse)
                .toList();
    }
}
