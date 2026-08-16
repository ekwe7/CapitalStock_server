package com.ekwe_hub.capitalstock_server.admin.dto.request;

import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;

public record UpdateMerchantStatusRequest(
    Merchant.MerchantStatus status
) {}
