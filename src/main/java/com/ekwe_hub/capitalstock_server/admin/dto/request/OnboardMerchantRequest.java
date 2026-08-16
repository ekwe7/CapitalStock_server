package com.ekwe_hub.capitalstock_server.admin.dto.request;

public record OnboardMerchantRequest(
    String name,
    String email,
    String businessPhone
) {}
