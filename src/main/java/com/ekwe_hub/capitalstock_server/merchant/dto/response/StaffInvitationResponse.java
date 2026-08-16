package com.ekwe_hub.capitalstock_server.merchant.dto.response;

import com.ekwe_hub.capitalstock_server.auth.model.Role;
import com.ekwe_hub.capitalstock_server.merchant.model.StaffInvitation;
import java.time.LocalDateTime;
import java.util.UUID;

public record StaffInvitationResponse(
    UUID id,
    UUID merchantId,
    String staffEmail,
    String staffFullName,
    Role.RoleName assignedRole,
    StaffInvitation.StaffInvitationStatus invitationStatus,
    LocalDateTime createdAt
) {}
