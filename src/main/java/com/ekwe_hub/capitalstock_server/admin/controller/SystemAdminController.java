package com.ekwe_hub.capitalstock_server.admin.controller;

import com.ekwe_hub.capitalstock_server.admin.dto.request.*;
import com.ekwe_hub.capitalstock_server.admin.dto.response.*;
import com.ekwe_hub.capitalstock_server.admin.service.SystemAdminService;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.CreateSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.VetSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
public class SystemAdminController {

    private final SystemAdminService systemAdminService;

    // --- Admin User Management ---
    @PostMapping
    public ResponseEntity<SystemAdminResponse> createSystemAdmin(@RequestBody CreateSystemAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(systemAdminService.createSystemAdmin(request));
    }

    @GetMapping
    public ResponseEntity<List<SystemAdminResponse>> getAllSystemAdmins() {
        return ResponseEntity.ok(systemAdminService.getAllSystemAdmins());
    }

    // --- System Health & Statistics ---
    @GetMapping("/stats")
    public ResponseEntity<SystemHealthStatsResponse> getSystemHealthStats() {
        return ResponseEntity.ok(systemAdminService.getSystemHealthStats());
    }

    // --- Merchant Onboarding & Status Management ---
    @PostMapping("/merchants")
    public ResponseEntity<MerchantResponse> onboardMerchant(@RequestBody OnboardMerchantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(systemAdminService.onboardMerchant(request));
    }

    @GetMapping("/merchants")
    public ResponseEntity<List<MerchantResponse>> getAllMerchants() {
        return ResponseEntity.ok(systemAdminService.getAllMerchants());
    }

    @GetMapping("/merchants/{id}")
    public ResponseEntity<MerchantResponse> getMerchantById(@PathVariable UUID id) {
        return ResponseEntity.ok(systemAdminService.getMerchantById(id));
    }

    @PatchMapping("/merchants/{id}/status")
    public ResponseEntity<MerchantResponse> updateMerchantStatus(
            @PathVariable UUID id,
            @RequestBody UpdateMerchantStatusRequest request
    ) {
        return ResponseEntity.ok(systemAdminService.updateMerchantStatus(id, request.status(), null));
    }

    // --- Supplier Vetting & Whitelisting ---
    @PostMapping("/suppliers")
    public ResponseEntity<SupplierResponse> createSupplier(@RequestBody CreateSupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(systemAdminService.createSupplier(request));
    }

    @PatchMapping("/suppliers/{id}/vet")
    public ResponseEntity<SupplierResponse> vetSupplier(
            @PathVariable UUID id,
            @RequestBody VetSupplierRequest request
    ) {
        return ResponseEntity.ok(systemAdminService.vetSupplier(id, request, null));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        return ResponseEntity.ok(systemAdminService.getAllSuppliers());
    }

    // --- Global Cryptographic Audit & Integrity Health ---
    @PostMapping("/audits/trigger")
    public ResponseEntity<GlobalAuditHealthResponse> triggerGlobalAudit() {
        return ResponseEntity.ok(systemAdminService.triggerGlobalAudit(null));
    }

    @GetMapping("/audits/global-health")
    public ResponseEntity<GlobalAuditHealthResponse> getGlobalAuditHealth() {
        return ResponseEntity.ok(systemAdminService.getGlobalAuditHealth());
    }

    // --- Platform Metrics & GMV Revenue ---
    @GetMapping("/metrics")
    public ResponseEntity<PlatformMetricsResponse> getPlatformMetrics() {
        return ResponseEntity.ok(systemAdminService.getPlatformMetrics());
    }
}
