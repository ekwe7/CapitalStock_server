package com.ekwe_hub.capitalstock_server.procurement.dto.request;

import java.util.List;
import java.util.UUID;

public record SubmitSupplierManifestRequest(
    UUID merchantId,
    String manifestNumber,
    List<SupplierManifestItemRequest> manifestItems
) {}
