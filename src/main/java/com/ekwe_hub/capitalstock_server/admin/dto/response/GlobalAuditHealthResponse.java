package com.ekwe_hub.capitalstock_server.admin.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record GlobalAuditHealthResponse(
    long totalLedgerBlocksAudited,
    long activeBreachAlertsCount,
    String overallIntegrityStatus, // e.g. "SECURE", "TAMPER_DETECTED"
    List<BreachAlertSummary> recentBreaches,
    LocalDateTime lastAuditedAt
) {
    public record BreachAlertSummary(
        String alertId,
        String merchantId,
        String alertType,
        String description,
        LocalDateTime createdAt
    ) {}
}
