package com.ekwe_hub.capitalstock_server.admin.dto.response;

public record SystemHealthStatsResponse(
    long totalMerchants,
    long activeMerchants,
    long suspendedMerchants,
    long pendingOnboardingMerchants,
    long deactivatedMerchants,
    String systemStatus
) {}
