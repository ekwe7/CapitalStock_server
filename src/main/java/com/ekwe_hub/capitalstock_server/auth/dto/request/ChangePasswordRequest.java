package com.ekwe_hub.capitalstock_server.auth.dto.request;

public record ChangePasswordRequest(
    String currentPassword,
    String newPassword
) {}
