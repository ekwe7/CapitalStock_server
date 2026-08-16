package com.ekwe_hub.capitalstock_server.admin.service.impl;

import com.ekwe_hub.capitalstock_server.admin.dto.request.*;
import com.ekwe_hub.capitalstock_server.admin.dto.response.*;
import com.ekwe_hub.capitalstock_server.admin.mapper.SystemAdminMapper;
import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;
import com.ekwe_hub.capitalstock_server.admin.repository.SystemAdminRepository;
import com.ekwe_hub.capitalstock_server.admin.service.SystemAdminService;
import com.ekwe_hub.capitalstock_server.common.events.GlobalAuditTriggeredEvent;
import com.ekwe_hub.capitalstock_server.common.events.MerchantStatusChangedEvent;
import com.ekwe_hub.capitalstock_server.ledger.model.AuditBreachAlert;
import com.ekwe_hub.capitalstock_server.ledger.repository.AuditBreachAlertRepository;
import com.ekwe_hub.capitalstock_server.merchant.mapper.MerchantMapper;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.CreateSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.VetSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;
import com.ekwe_hub.capitalstock_server.procurement.service.SupplierService;
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
public class SystemAdminServiceImpl implements SystemAdminService {

    private final SystemAdminRepository systemAdminRepository;
    private final MerchantRepository merchantRepository;
    private final SupplierService supplierService;
    private final AuditBreachAlertRepository auditBreachAlertRepository;
    private final SystemAdminMapper systemAdminMapper;
    private final MerchantMapper merchantMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SystemAdminResponse createSystemAdmin(CreateSystemAdminRequest request) {
        if (systemAdminRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("System Admin email already exists: " + request.email());
        }

        SystemAdmin admin = systemAdminMapper.toEntity(request);
        return systemAdminMapper.toResponse(systemAdminRepository.save(admin));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemAdminResponse> getAllSystemAdmins() {
        return systemAdminRepository.findAll()
                .stream()
                .map(systemAdminMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MerchantResponse onboardMerchant(OnboardMerchantRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Merchant email already exists: " + request.email());
        }

        Merchant merchant = merchantMapper.toEntity(request);
        return merchantMapper.toResponse(merchantRepository.save(merchant));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll()
                .stream()
                .map(merchantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantResponse getMerchantById(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found with ID: " + id));
        return merchantMapper.toResponse(merchant);
    }

    @Override
    @Transactional
    public MerchantResponse updateMerchantStatus(UUID id, Merchant.MerchantStatus newStatus, UUID adminId) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found with ID: " + id));

        Merchant.MerchantStatus previousStatus = merchant.getStatus();
        merchant.setStatus(newStatus);
        Merchant updatedMerchant = merchantRepository.save(merchant);

        // Publish MerchantStatusChangedEvent
        eventPublisher.publishEvent(new MerchantStatusChangedEvent(
                updatedMerchant.getId(),
                previousStatus,
                updatedMerchant.getStatus(),
                adminId,
                LocalDateTime.now()
        ));

        return merchantMapper.toResponse(updatedMerchant);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemHealthStatsResponse getSystemHealthStats() {
        long totalMerchants = merchantRepository.count();
        long activeMerchants = merchantRepository.countByStatus(Merchant.MerchantStatus.ACTIVE);
        long suspendedMerchants = merchantRepository.countByStatus(Merchant.MerchantStatus.SUSPENDED);
        long pendingOnboardingMerchants = merchantRepository.countByStatus(Merchant.MerchantStatus.PENDING_ONBOARDING);
        long deactivatedMerchants = merchantRepository.countByStatus(Merchant.MerchantStatus.DEACTIVATED);

        return new SystemHealthStatsResponse(
                totalMerchants,
                activeMerchants,
                suspendedMerchants,
                pendingOnboardingMerchants,
                deactivatedMerchants,
                "HEALTHY"
        );
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        return supplierService.createSupplier(request);
    }

    @Override
    @Transactional
    public SupplierResponse vetSupplier(UUID supplierId, VetSupplierRequest request, UUID adminId) {
        return supplierService.vetSupplier(supplierId, request, adminId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @Override
    @Transactional
    public GlobalAuditHealthResponse triggerGlobalAudit(UUID adminId) {
        long totalMerchantsAudited = merchantRepository.count();
        List<AuditBreachAlert> activeBreaches = auditBreachAlertRepository.findByResolvedFalse();

        eventPublisher.publishEvent(new GlobalAuditTriggeredEvent(
                adminId,
                totalMerchantsAudited,
                activeBreaches.size(),
                LocalDateTime.now()
        ));

        return buildAuditHealthResponse(activeBreaches);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalAuditHealthResponse getGlobalAuditHealth() {
        List<AuditBreachAlert> activeBreaches = auditBreachAlertRepository.findByResolvedFalse();
        return buildAuditHealthResponse(activeBreaches);
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformMetricsResponse getPlatformMetrics() {
        long totalVettedSuppliers = supplierService.getAllSuppliers().size();

        return new PlatformMetricsResponse(
                BigDecimal.ZERO, // GMV initialized to 0.00 until sales module runs transactions
                BigDecimal.ZERO, // Platform revenue initialized to 0.00
                0L,
                0L,
                totalVettedSuppliers
        );
    }

    private GlobalAuditHealthResponse buildAuditHealthResponse(List<AuditBreachAlert> breaches) {
        String status = breaches.isEmpty() ? "SECURE" : "TAMPER_DETECTED";
        var breachSummaries = breaches.stream()
                .map(b -> new GlobalAuditHealthResponse.BreachAlertSummary(
                        b.getId().toString(),
                        b.getMerchantId().toString(),
                        b.getAlertType(),
                        b.getDescription(),
                        b.getCreatedAt()
                )).toList();

        return new GlobalAuditHealthResponse(
                0L, // Total ledger blocks
                breaches.size(),
                status,
                breachSummaries,
                LocalDateTime.now()
        );
    }
}
