package com.ekwe_hub.capitalstock_server.admin.dto.response;

import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import java.time.LocalDateTime;
import java.util.UUID;

public record MerchantResponse(
    UUID id,
    String name,
    String email,
    String businessPhone,
    Merchant.MerchantStatus status,
    LocalDateTime createdAt
) {}
