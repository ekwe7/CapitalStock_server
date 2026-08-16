package com.ekwe_hub.capitalstock_server.merchant.service.impl;

import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.common.events.MerchantProfileUpdatedEvent;
import com.ekwe_hub.capitalstock_server.common.events.StaffInvitedEvent;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import com.ekwe_hub.capitalstock_server.inventory.repository.ProductRepository;
import com.ekwe_hub.capitalstock_server.merchant.dto.request.*;
import com.ekwe_hub.capitalstock_server.merchant.dto.response.*;
import com.ekwe_hub.capitalstock_server.merchant.mapper.MerchantMapper;
import com.ekwe_hub.capitalstock_server.merchant.mapper.StaffInvitationMapper;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import com.ekwe_hub.capitalstock_server.merchant.model.StaffInvitation;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import com.ekwe_hub.capitalstock_server.merchant.repository.StaffInvitationRepository;
import com.ekwe_hub.capitalstock_server.merchant.service.MerchantManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantManagementServiceImpl implements MerchantManagementService {

    private final MerchantRepository merchantRepository;
    private final StaffInvitationRepository staffInvitationRepository;
    private final ProductRepository productRepository;
    private final MerchantMapper merchantMapper;
    private final StaffInvitationMapper staffInvitationMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MerchantResponse updateMerchantBusinessProfile(UUID merchantId, UpdateMerchantProfileRequest request, UUID updatedByUserId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Merchant business record not found for ID: " + merchantId));

        if (request.businessName() != null && !request.businessName().isBlank()) {
            merchant.setName(request.businessName());
        }
        if (request.businessPhone() != null) {
            merchant.setBusinessPhone(request.businessPhone());
        }
        if (request.registrationNumberRcNumber() != null) {
            merchant.setRegistrationNumberRcNumber(request.registrationNumberRcNumber());
        }

        Merchant updatedMerchant = merchantRepository.save(merchant);

        eventPublisher.publishEvent(new MerchantProfileUpdatedEvent(
                updatedMerchant.getId(),
                updatedMerchant.getName(),
                updatedMerchant.getRegistrationNumberRcNumber(),
                updatedByUserId,
                LocalDateTime.now()
        ));

        return merchantMapper.toResponse(updatedMerchant);
    }

    @Override
    @Transactional
    public MerchantResponse configureMerchantPaymentSubaccounts(UUID merchantId, ConfigurePaymentSubaccountsRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Merchant business record not found for ID: " + merchantId));

        if (request.paystackSubaccountCode() != null) {
            merchant.setPaystackSubaccountCode(request.paystackSubaccountCode());
        }
        if (request.flutterwaveSubaccountCode() != null) {
            merchant.setFlutterwaveSubaccountCode(request.flutterwaveSubaccountCode());
        }

        return merchantMapper.toResponse(merchantRepository.save(merchant));
    }

    @Override
    @Transactional
    public StaffInvitationResponse inviteMerchantStoreStaffMember(UUID merchantId, InviteStaffMemberRequest request, UUID invitedByUserId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new IllegalArgumentException("Merchant business record not found for ID: " + merchantId);
        }

        if (staffInvitationRepository.existsByMerchantIdAndStaffEmail(merchantId, request.staffEmail())) {
            throw new IllegalArgumentException("Staff member email already invited: " + request.staffEmail());
        }

        StaffInvitation invitation = staffInvitationMapper.toEntity(request, merchantId);
        StaffInvitation savedInvitation = staffInvitationRepository.save(invitation);

        eventPublisher.publishEvent(new StaffInvitedEvent(
                savedInvitation.getId(),
                savedInvitation.getMerchantId(),
                savedInvitation.getStaffEmail(),
                savedInvitation.getStaffFullName(),
                savedInvitation.getAssignedRole().name(),
                invitedByUserId,
                LocalDateTime.now()
        ));

        return staffInvitationMapper.toResponse(savedInvitation);
    }

    @Override
    @Transactional
    public StaffInvitationResponse updateMerchantStoreStaffInvitationStatus(UUID merchantId, UUID invitationId, UpdateStaffInvitationStatusRequest request) {
        StaffInvitation invitation = staffInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Staff invitation record not found for ID: " + invitationId));

        if (!invitation.getMerchantId().equals(merchantId)) {
            throw new IllegalArgumentException("Staff invitation does not belong to specified merchant ID");
        }

        invitation.setInvitationStatus(request.invitationStatus());
        return staffInvitationMapper.toResponse(staffInvitationRepository.save(invitation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffInvitationResponse> retrieveAllMerchantStoreStaffInvitations(UUID merchantId) {
        return staffInvitationRepository.findByMerchantId(merchantId)
                .stream()
                .map(staffInvitationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantStockValuationAnalyticsResponse calculateMerchantStockValuationAndAnalytics(UUID merchantId) {
        List<Product> products = productRepository.findByMerchantId(merchantId);

        BigDecimal totalCostValuation = products.stream()
                .map(p -> p.getCostPrice().multiply(BigDecimal.valueOf(p.getAvailableQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSellingValuation = products.stream()
                .map(p -> p.getSellingPrice().multiply(BigDecimal.valueOf(p.getAvailableQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalAvailableItems = products.stream().mapToLong(Product::getAvailableQuantity).sum();
        long totalReservedItems = products.stream().mapToLong(Product::getReservedQuantity).sum();

        long unverifiedCount = products.stream()
                .filter(p -> p.getVerificationStatus() == Product.StockVerificationStatus.UNVERIFIED_MANUAL)
                .count();

        long verifiedCount = products.stream()
                .filter(p -> p.getVerificationStatus() == Product.StockVerificationStatus.VERIFIED_SUPPLIER_STOCK)
                .count();

        return new MerchantStockValuationAnalyticsResponse(
                merchantId,
                totalCostValuation,
                totalSellingValuation,
                products.size(),
                totalAvailableItems,
                totalReservedItems,
                unverifiedCount,
                verifiedCount
        );
    }
}
