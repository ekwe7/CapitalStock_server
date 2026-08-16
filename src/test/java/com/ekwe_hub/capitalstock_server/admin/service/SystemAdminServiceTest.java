package com.ekwe_hub.capitalstock_server.admin.service;

import com.ekwe_hub.capitalstock_server.admin.dto.request.*;
import com.ekwe_hub.capitalstock_server.admin.dto.response.*;
import com.ekwe_hub.capitalstock_server.admin.mapper.SystemAdminMapper;
import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;
import com.ekwe_hub.capitalstock_server.admin.repository.SystemAdminRepository;
import com.ekwe_hub.capitalstock_server.admin.service.impl.SystemAdminServiceImpl;
import com.ekwe_hub.capitalstock_server.ledger.repository.AuditBreachAlertRepository;
import com.ekwe_hub.capitalstock_server.merchant.mapper.MerchantMapper;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import com.ekwe_hub.capitalstock_server.procurement.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemAdminServiceTest {

    @Mock
    private SystemAdminRepository systemAdminRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private SupplierService supplierService;

    @Mock
    private AuditBreachAlertRepository auditBreachAlertRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private SystemAdminMapper systemAdminMapper;

    @Spy
    private MerchantMapper merchantMapper;

    @InjectMocks
    private SystemAdminServiceImpl systemAdminService;

    private SystemAdmin admin;
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        admin = SystemAdmin.builder()
                .id(UUID.randomUUID())
                .fullName("Root Admin")
                .email("admin@veritastock.com")
                .passwordHash("secret123")
                .role(SystemAdmin.AdminRole.SUPER_ADMIN)
                .status(SystemAdmin.AdminStatus.ACTIVE)
                .build();

        merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .name("Alaba Tech Vendor")
                .email("vendor@alaba.com")
                .businessPhone("+2348000000000")
                .status(Merchant.MerchantStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldCreateSystemAdminSuccessfully() {
        CreateSystemAdminRequest request = new CreateSystemAdminRequest(
                "Root Admin", "admin@veritastock.com", "secret123", SystemAdmin.AdminRole.SUPER_ADMIN
        );

        when(systemAdminRepository.existsByEmail(request.email())).thenReturn(false);
        when(systemAdminRepository.save(any(SystemAdmin.class))).thenReturn(admin);

        SystemAdminResponse response = systemAdminService.createSystemAdmin(request);

        assertNotNull(response);
        assertEquals("admin@veritastock.com", response.email());
        assertEquals(SystemAdmin.AdminRole.SUPER_ADMIN, response.role());
    }

    @Test
    void shouldOnboardMerchantSuccessfully() {
        OnboardMerchantRequest request = new OnboardMerchantRequest(
                "Alaba Tech Vendor", "vendor@alaba.com", "+2348000000000"
        );

        when(merchantRepository.existsByEmail(request.email())).thenReturn(false);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        MerchantResponse response = systemAdminService.onboardMerchant(request);

        assertNotNull(response);
        assertEquals("vendor@alaba.com", response.email());
    }

    @Test
    void shouldUpdateMerchantStatusAndPublishEvent() {
        UUID merchantId = merchant.getId();
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));
        when(merchantRepository.save(any(Merchant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MerchantResponse response = systemAdminService.updateMerchantStatus(merchantId, Merchant.MerchantStatus.SUSPENDED, admin.getId());

        assertEquals(Merchant.MerchantStatus.SUSPENDED, response.status());
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void shouldTriggerGlobalAudit() {
        when(merchantRepository.count()).thenReturn(5L);
        when(auditBreachAlertRepository.findByResolvedFalse()).thenReturn(List.of());

        GlobalAuditHealthResponse health = systemAdminService.triggerGlobalAudit(admin.getId());

        assertNotNull(health);
        assertEquals("SECURE", health.overallIntegrityStatus());
        assertEquals(0, health.activeBreachAlertsCount());
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }
}
