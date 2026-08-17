package com.ekwe_hub.capitalstock_server.procurement.controller;

import com.ekwe_hub.capitalstock_server.procurement.dto.request.*;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.*;
import com.ekwe_hub.capitalstock_server.procurement.service.SupplierManifestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplierManifestController {

    private final SupplierManifestService supplierManifestService;

    @PostMapping("/suppliers/{supplierId}/manifests")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<SupplierInvoiceManifestResponse> submitSupplierInvoiceManifest(
            @PathVariable UUID supplierId,
            @RequestBody SubmitSupplierManifestRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierManifestService.submitSupplierInvoiceManifest(supplierId, request));
    }

    @PostMapping("/manifests/{manifestId}/scan")
    @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<SupplierInvoiceManifestResponse> scanInwardDeliveryBarcode(
            @PathVariable UUID manifestId,
            @RequestBody ScanInwardBarcodeRequest request
    ) {
        return ResponseEntity.ok(supplierManifestService.scanInwardDeliveryBarcode(manifestId, request, null));
    }

    @PostMapping("/manifests/{manifestId}/finalize")
    @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<SupplierInvoiceManifestResponse> finalizeAndReconcileInwardDeliveryManifest(
            @PathVariable UUID manifestId
    ) {
        return ResponseEntity.ok(supplierManifestService.finalizeAndReconcileInwardDeliveryManifest(manifestId, null));
    }

    @GetMapping("/manifests/{manifestId}/progress")
    @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_SUPPLIER') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<ManifestCheckinProgressResponse> retrieveManifestCheckinProgress(
            @PathVariable UUID manifestId
    ) {
        return ResponseEntity.ok(supplierManifestService.retrieveManifestCheckinProgress(manifestId));
    }

    @GetMapping("/manifests/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT_ADMIN') or hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<SupplierInvoiceManifestResponse>> retrieveManifestsByMerchantId(
            @PathVariable UUID merchantId
    ) {
        return ResponseEntity.ok(supplierManifestService.retrieveManifestsByMerchantId(merchantId));
    }

    @GetMapping("/manifests/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<List<SupplierInvoiceManifestResponse>> retrieveManifestsBySupplierId(
            @PathVariable UUID supplierId
    ) {
        return ResponseEntity.ok(supplierManifestService.retrieveManifestsBySupplierId(supplierId));
    }
}
