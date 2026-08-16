package com.ekwe_hub.capitalstock_server.merchant.dto.request;

public record UpdateMerchantProfileRequest(
    String businessName,
    String businessPhone,
    String registrationNumberRcNumber
) {}
