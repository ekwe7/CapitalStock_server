package com.ekwe_hub.capitalstock_server.merchant.service;

import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.merchant.dto.request.*;
import com.ekwe_hub.capitalstock_server.merchant.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface MerchantManagementService {
    MerchantResponse updateMerchantBusinessProfile(UUID merchantId, UpdateMerchantProfileRequest request, UUID updatedByUserId);
    MerchantResponse configureMerchantPaymentSubaccounts(UUID merchantId, ConfigurePaymentSubaccountsRequest request);
    StaffInvitationResponse inviteMerchantStoreStaffMember(UUID merchantId, InviteStaffMemberRequest request, UUID invitedByUserId);
    StaffInvitationResponse updateMerchantStoreStaffInvitationStatus(UUID merchantId, UUID invitationId, UpdateStaffInvitationStatusRequest request);
    List<StaffInvitationResponse> retrieveAllMerchantStoreStaffInvitations(UUID merchantId);
    MerchantStockValuationAnalyticsResponse calculateMerchantStockValuationAndAnalytics(UUID merchantId);
}
