package com.ekwe_hub.capitalstock_server.procurement.service;

import com.ekwe_hub.capitalstock_server.procurement.dto.request.*;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface SupplierManifestService {
    SupplierInvoiceManifestResponse submitSupplierInvoiceManifest(UUID supplierId, SubmitSupplierManifestRequest request);
    SupplierInvoiceManifestResponse scanInwardDeliveryBarcode(UUID manifestId, ScanInwardBarcodeRequest request, UUID scannedByUserId);
    SupplierInvoiceManifestResponse finalizeAndReconcileInwardDeliveryManifest(UUID manifestId, UUID finalizedByUserId);
    ManifestCheckinProgressResponse retrieveManifestCheckinProgress(UUID manifestId);
    List<SupplierInvoiceManifestResponse> retrieveManifestsByMerchantId(UUID merchantId);
    List<SupplierInvoiceManifestResponse> retrieveManifestsBySupplierId(UUID supplierId);
    SupplierInvoiceManifestResponse retrieveManifestById(UUID manifestId);
}
