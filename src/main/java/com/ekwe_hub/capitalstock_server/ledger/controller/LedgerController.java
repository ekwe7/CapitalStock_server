package com.ekwe_hub.capitalstock_server.ledger.controller;

import com.ekwe_hub.capitalstock_server.ledger.dto.response.*;
import com.ekwe_hub.capitalstock_server.ledger.service.CryptographicLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final CryptographicLedgerService cryptographicLedgerService;

    @GetMapping("/merchants/{merchantId}/audit-verification")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_MERCHANT_ADMIN')")
    public ResponseEntity<LedgerAuditVerificationResponse> verifyMerchantLedgerCryptographicIntegrity(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(cryptographicLedgerService.verifyMerchantLedgerCryptographicIntegrity(merchantId));
    }

    @PostMapping("/merchants/{merchantId}/trigger-audit")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_MERCHANT_ADMIN')")
    public ResponseEntity<LedgerAuditVerificationResponse> triggerMerchantLedgerAudit(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(cryptographicLedgerService.verifyMerchantLedgerCryptographicIntegrity(merchantId));
    }

    @GetMapping("/merchants/{merchantId}/blocks")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_MERCHANT_ADMIN')")
    public ResponseEntity<List<LedgerBlockResponse>> retrieveLedgerBlocksForMerchant(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(cryptographicLedgerService.retrieveLedgerBlocksForMerchant(merchantId));
    }

    @GetMapping("/merchants/{merchantId}/alerts")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or hasAuthority('ROLE_MERCHANT_ADMIN')")
    public ResponseEntity<List<AuditBreachAlertResponse>> retrieveAuditBreachAlertsForMerchant(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(cryptographicLedgerService.retrieveAuditBreachAlertsForMerchant(merchantId));
    }
}
