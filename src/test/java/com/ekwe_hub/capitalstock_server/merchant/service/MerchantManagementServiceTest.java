package com.ekwe_hub.capitalstock_server.merchant.service;

import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.auth.model.Role;
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
import com.ekwe_hub.capitalstock_server.merchant.service.impl.MerchantManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantManagementServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private StaffInvitationRepository staffInvitationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private MerchantMapper merchantMapper;

    @Spy
    private StaffInvitationMapper staffInvitationMapper;

    @InjectMocks
    private MerchantManagementServiceImpl merchantManagementService;

    private Merchant merchant;

    @BeforeEach
    void setUp() {
        merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .name("Alaba Retail Hub")
                .email("admin@alabaretail.com")
                .businessPhone("+2348011111111")
                .status(Merchant.MerchantStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldUpdateMerchantBusinessProfileSuccessfully() {
        UUID merchantId = merchant.getId();
        UpdateMerchantProfileRequest request = new UpdateMerchantProfileRequest(
                "Alaba Electronics Market Store", "+2348099999999", "RC12345678"
        );

        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));
        when(merchantRepository.save(any(Merchant.class))).thenAnswer(i -> i.getArgument(0));

        MerchantResponse response = merchantManagementService.updateMerchantBusinessProfile(merchantId, request, UUID.randomUUID());

        assertNotNull(response);
        assertEquals("Alaba Electronics Market Store", response.name());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldInviteStoreStaffMemberSuccessfully() {
        UUID merchantId = merchant.getId();
        InviteStaffMemberRequest request = new InviteStaffMemberRequest(
                "cashier@alabaretail.com", "John Staff", Role.RoleName.ROLE_STORE_STAFF
        );

        StaffInvitation invitation = StaffInvitation.builder()
                .id(UUID.randomUUID())
                .merchantId(merchantId)
                .staffEmail(request.staffEmail())
                .staffFullName(request.staffFullName())
                .assignedRole(request.assignedRole())
                .invitationStatus(StaffInvitation.StaffInvitationStatus.PENDING)
                .build();

        when(merchantRepository.existsById(merchantId)).thenReturn(true);
        when(staffInvitationRepository.existsByMerchantIdAndStaffEmail(merchantId, request.staffEmail())).thenReturn(false);
        when(staffInvitationRepository.save(any())).thenReturn(invitation);

        StaffInvitationResponse response = merchantManagementService.inviteMerchantStoreStaffMember(merchantId, request, UUID.randomUUID());

        assertNotNull(response);
        assertEquals("cashier@alabaretail.com", response.staffEmail());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldCalculateStockValuationAnalytics() {
        UUID merchantId = merchant.getId();
        Product p1 = Product.builder()
                .id(UUID.randomUUID())
                .merchantId(merchantId)
                .barcode("11111")
                .name("Laptop")
                .costPrice(BigDecimal.valueOf(100000))
                .sellingPrice(BigDecimal.valueOf(150000))
                .availableQuantity(5)
                .reservedQuantity(1)
                .verificationStatus(Product.StockVerificationStatus.UNVERIFIED_MANUAL)
                .build();

        when(productRepository.findByMerchantId(merchantId)).thenReturn(List.of(p1));

        MerchantStockValuationAnalyticsResponse analytics = merchantManagementService.calculateMerchantStockValuationAndAnalytics(merchantId);

        assertNotNull(analytics);
        assertEquals(BigDecimal.valueOf(500000), analytics.totalInventoryCostValuationAmount());
        assertEquals(BigDecimal.valueOf(750000), analytics.totalPotentialSellingValuationAmount());
        assertEquals(1, analytics.totalUniqueProductSkusCount());
        assertEquals(1, analytics.unverifiedManualStockItemsCount());
    }
}
