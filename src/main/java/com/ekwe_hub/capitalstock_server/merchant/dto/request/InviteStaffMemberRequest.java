package com.ekwe_hub.capitalstock_server.merchant.dto.request;

import com.ekwe_hub.capitalstock_server.auth.model.Role;

public record InviteStaffMemberRequest(
    String staffEmail,
    String staffFullName,
    Role.RoleName assignedRole
) {}
