package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record GlobalAuditTriggeredEvent(
    UUID triggeredByAdminId,
    long totalMerchantsAudited,
    long totalBreachesDetected,
    LocalDateTime timestamp
) {}
