package com.ekwe_hub.capitalstock_server.ledger.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerAuditVerificationResponse(
    UUID merchantId,
    boolean isLedgerIntegrityVerifiedIntact,
    long totalBlocksAuditedCount,
    long totalTamperedBlocksDetectedCount,
    String auditSummaryMessage,
    LocalDateTime auditTimestamp
) {}
