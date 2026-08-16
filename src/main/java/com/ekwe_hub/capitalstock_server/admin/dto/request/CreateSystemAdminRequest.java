package com.ekwe_hub.capitalstock_server.admin.dto.request;

import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;

public record CreateSystemAdminRequest(
    String fullName,
    String email,
    String password,
    SystemAdmin.AdminRole role
) {}
