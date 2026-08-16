package com.ekwe_hub.capitalstock_server.merchant.controller;

import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.merchant.dto.request.*;
import com.ekwe_hub.capitalstock_server.merchant.dto.response.*;
import com.ekwe_hub.capitalstock_server.merchant.service.MerchantManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantManagementService merchantManagementService;

    @PutMapping("/{merchantId}/profile")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<MerchantResponse> updateMerchantBusinessProfile(
            @PathVariable UUID merchantId,
            @RequestBody UpdateMerchantProfileRequest request
    ) {
        return ResponseEntity.ok(merchantManagementService.updateMerchantBusinessProfile(merchantId, request, null));
    }

    @PutMapping("/{merchantId}/payment-subaccounts")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<MerchantResponse> configureMerchantPaymentSubaccounts(
            @PathVariable UUID merchantId,
            @RequestBody ConfigurePaymentSubaccountsRequest request
    ) {
        return ResponseEntity.ok(merchantManagementService.configureMerchantPaymentSubaccounts(merchantId, request));
    }

    @PostMapping("/{merchantId}/staff")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<StaffInvitationResponse> inviteMerchantStoreStaffMember(
            @PathVariable UUID merchantId,
            @RequestBody InviteStaffMemberRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(merchantManagementService.inviteMerchantStoreStaffMember(merchantId, request, null));
    }

    @PatchMapping("/{merchantId}/staff/{invitationId}/status")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<StaffInvitationResponse> updateMerchantStoreStaffInvitationStatus(
            @PathVariable UUID merchantId,
            @PathVariable UUID invitationId,
            @RequestBody UpdateStaffInvitationStatusRequest request
    ) {
        return ResponseEntity.ok(merchantManagementService.updateMerchantStoreStaffInvitationStatus(merchantId, invitationId, request));
    }

    @GetMapping("/{merchantId}/staff")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<StaffInvitationResponse>> retrieveAllMerchantStoreStaffInvitations(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(merchantManagementService.retrieveAllMerchantStoreStaffInvitations(merchantId));
    }

    @GetMapping("/{merchantId}/analytics/stock-valuation")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<MerchantStockValuationAnalyticsResponse> calculateMerchantStockValuationAndAnalytics(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(merchantManagementService.calculateMerchantStockValuationAndAnalytics(merchantId));
    }
}
