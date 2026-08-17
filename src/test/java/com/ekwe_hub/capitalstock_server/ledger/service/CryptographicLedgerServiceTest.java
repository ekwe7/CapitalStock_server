package com.ekwe_hub.capitalstock_server.ledger.service;

import com.ekwe_hub.capitalstock_server.ledger.dto.request.AppendStockMutationLedgerBlockRequest;
import com.ekwe_hub.capitalstock_server.ledger.dto.response.*;
import com.ekwe_hub.capitalstock_server.ledger.mapper.InventoryCryptographicLedgerMapper;
import com.ekwe_hub.capitalstock_server.ledger.model.AuditBreachAlert;
import com.ekwe_hub.capitalstock_server.ledger.model.HashProof;
import com.ekwe_hub.capitalstock_server.ledger.model.InventoryCryptographicLedger;
import com.ekwe_hub.capitalstock_server.ledger.repository.AuditBreachAlertRepository;
import com.ekwe_hub.capitalstock_server.ledger.repository.InventoryCryptographicLedgerRepository;
import com.ekwe_hub.capitalstock_server.ledger.service.impl.CryptographicLedgerServiceImpl;
import com.ekwe_hub.capitalstock_server.ledger.util.Sha256CryptographicHashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptographicLedgerServiceTest {

    @Mock
    private InventoryCryptographicLedgerRepository ledgerRepository;

    @Mock
    private AuditBreachAlertRepository auditBreachAlertRepository;

    @Spy
    private Sha256CryptographicHashGenerator hashGenerator;

    @Spy
    private InventoryCryptographicLedgerMapper ledgerMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CryptographicLedgerServiceImpl cryptographicLedgerService;

    private UUID merchantId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        merchantId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void shouldAppendSequentialSHA256SignedLedgerBlock() {
        AppendStockMutationLedgerBlockRequest request = new AppendStockMutationLedgerBlockRequest(
                merchantId, productId, "SUPPLIER_CHECKIN", 10, 10
        );

        when(ledgerRepository.findTopByMerchantIdOrderBySequenceNumberDesc(merchantId)).thenReturn(Optional.empty());
        when(ledgerRepository.save(any(InventoryCryptographicLedger.class))).thenAnswer(i -> i.getArgument(0));

        LedgerBlockResponse response = cryptographicLedgerService.appendInventoryMutationLedgerBlock(request);

        assertNotNull(response);
        assertEquals(1L, response.sequenceNumber());
        assertNotNull(response.recordPayloadHash());
        assertNotNull(response.currentSignatureHash());
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", response.previousHash());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldVerifyIntactLedgerHashChainContinuityCleanly() {
        LocalDateTime now = LocalDateTime.now();
        String payload1 = hashGenerator.computePayloadHash(1L, merchantId, productId, "SUPPLIER_CHECKIN", 10, 10, now.toString());
        String sig1 = hashGenerator.computeSignatureHash("0000000000000000000000000000000000000000000000000000000000000000", payload1);

        InventoryCryptographicLedger block1 = InventoryCryptographicLedger.builder()
                .id(UUID.randomUUID())
                .merchantId(merchantId)
                .sequenceNumber(1L)
                .productId(productId)
                .eventType("SUPPLIER_CHECKIN")
                .quantityChange(10)
                .resultingBalance(10)
                .hashProof(new HashProof(payload1, "0000000000000000000000000000000000000000000000000000000000000000", sig1))
                .createdAt(now)
                .build();

        when(ledgerRepository.findByMerchantIdOrderBySequenceNumberAsc(merchantId)).thenReturn(List.of(block1));

        LedgerAuditVerificationResponse audit = cryptographicLedgerService.verifyMerchantLedgerCryptographicIntegrity(merchantId);

        assertNotNull(audit);
        assertTrue(audit.isLedgerIntegrityVerifiedIntact());
        assertEquals(1L, audit.totalBlocksAuditedCount());
        assertEquals(0L, audit.totalTamperedBlocksDetectedCount());
    }

    @Test
    void shouldDetectTamperingAndTriggerAlertWhenHistoricalRowIsAltered() {
        LocalDateTime now = LocalDateTime.now();
        // Malicious user changed quantity in DB without recomputing signature
        HashProof tamperedProof = new HashProof("corruptedPayloadHash", "0000000000000000000000000000000000000000000000000000000000000000", "invalidSignatureHash");

        InventoryCryptographicLedger tamperedBlock = InventoryCryptographicLedger.builder()
                .id(UUID.randomUUID())
                .merchantId(merchantId)
                .sequenceNumber(1L)
                .productId(productId)
                .eventType("POS_PHYSICAL_SALE")
                .quantityChange(-5)
                .resultingBalance(5)
                .hashProof(tamperedProof)
                .createdAt(now)
                .build();

        when(ledgerRepository.findByMerchantIdOrderBySequenceNumberAsc(merchantId)).thenReturn(List.of(tamperedBlock));
        when(auditBreachAlertRepository.save(any())).thenAnswer(i -> {
            AuditBreachAlert a = i.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        LedgerAuditVerificationResponse audit = cryptographicLedgerService.verifyMerchantLedgerCryptographicIntegrity(merchantId);

        assertNotNull(audit);
        assertFalse(audit.isLedgerIntegrityVerifiedIntact());
        assertEquals(1L, audit.totalTamperedBlocksDetectedCount());
        verify(auditBreachAlertRepository).save(any());
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
