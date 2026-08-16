package com.ekwe_hub.capitalstock_server.admin.dto.response;

import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;
import java.time.LocalDateTime;
import java.util.UUID;

public record SystemAdminResponse(
    UUID id,
    String fullName,
    String email,
    SystemAdmin.AdminRole role,
    SystemAdmin.AdminStatus status,
    LocalDateTime createdAt
) {}
