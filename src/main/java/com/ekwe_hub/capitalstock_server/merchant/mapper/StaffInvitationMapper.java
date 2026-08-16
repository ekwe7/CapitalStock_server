package com.ekwe_hub.capitalstock_server.merchant.mapper;

import com.ekwe_hub.capitalstock_server.merchant.dto.request.InviteStaffMemberRequest;
import com.ekwe_hub.capitalstock_server.merchant.dto.response.StaffInvitationResponse;
import com.ekwe_hub.capitalstock_server.merchant.model.StaffInvitation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StaffInvitationMapper {

    public StaffInvitation toEntity(InviteStaffMemberRequest request, UUID merchantId) {
        if (request == null) return null;
        return StaffInvitation.builder()
                .merchantId(merchantId)
                .staffEmail(request.staffEmail())
                .staffFullName(request.staffFullName())
                .assignedRole(request.assignedRole())
                .invitationStatus(StaffInvitation.StaffInvitationStatus.PENDING)
                .build();
    }

    public StaffInvitationResponse toResponse(StaffInvitation entity) {
        if (entity == null) return null;
        return new StaffInvitationResponse(
                entity.getId(),
                entity.getMerchantId(),
                entity.getStaffEmail(),
                entity.getStaffFullName(),
                entity.getAssignedRole(),
                entity.getInvitationStatus(),
                entity.getCreatedAt()
        );
    }
}
