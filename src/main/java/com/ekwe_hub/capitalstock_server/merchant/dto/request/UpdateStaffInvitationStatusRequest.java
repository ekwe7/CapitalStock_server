package com.ekwe_hub.capitalstock_server.merchant.dto.request;

import com.ekwe_hub.capitalstock_server.merchant.model.StaffInvitation;

public record UpdateStaffInvitationStatusRequest(
    StaffInvitation.StaffInvitationStatus invitationStatus
) {}
