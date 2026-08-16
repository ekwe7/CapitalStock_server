package com.ekwe_hub.capitalstock_server.common.events;

import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import java.time.LocalDateTime;
import java.util.UUID;

public record MerchantStatusChangedEvent(
    UUID merchantId,
    Merchant.MerchantStatus previousStatus,
    Merchant.MerchantStatus newStatus,
    UUID updatedByAdminId,
    LocalDateTime timestamp
) {}
