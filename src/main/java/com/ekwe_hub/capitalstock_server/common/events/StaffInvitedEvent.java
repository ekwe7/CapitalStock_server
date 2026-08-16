package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record StaffInvitedEvent(
    UUID invitationId,
    UUID merchantId,
    String staffEmail,
    String staffFullName,
    String assignedRoleName,
    UUID invitedByMerchantAdminId,
    LocalDateTime timestamp
) {}
