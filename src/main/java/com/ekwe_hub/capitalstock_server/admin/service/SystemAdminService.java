package com.ekwe_hub.capitalstock_server.admin.service;

import com.ekwe_hub.capitalstock_server.admin.dto.request.*;
import com.ekwe_hub.capitalstock_server.admin.dto.response.*;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.CreateSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.VetSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface SystemAdminService {
    SystemAdminResponse createSystemAdmin(CreateSystemAdminRequest request);
    List<SystemAdminResponse> getAllSystemAdmins();
    MerchantResponse onboardMerchant(OnboardMerchantRequest request);
    List<MerchantResponse> getAllMerchants();
    MerchantResponse getMerchantById(UUID id);
    MerchantResponse updateMerchantStatus(UUID id, Merchant.MerchantStatus newStatus, UUID adminId);
    SystemHealthStatsResponse getSystemHealthStats();

    // Supplier Vetting
    SupplierResponse createSupplier(CreateSupplierRequest request);
    SupplierResponse vetSupplier(UUID supplierId, VetSupplierRequest request, UUID adminId);
    List<SupplierResponse> getAllSuppliers();

    // Global Cryptographic Audit & Metrics
    GlobalAuditHealthResponse triggerGlobalAudit(UUID adminId);
    GlobalAuditHealthResponse getGlobalAuditHealth();
    PlatformMetricsResponse getPlatformMetrics();
}
