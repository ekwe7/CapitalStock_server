package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record MerchantProfileUpdatedEvent(
    UUID merchantId,
    String updatedBusinessName,
    String registrationNumberRcNumber,
    UUID updatedByUserId,
    LocalDateTime timestamp
) {}
