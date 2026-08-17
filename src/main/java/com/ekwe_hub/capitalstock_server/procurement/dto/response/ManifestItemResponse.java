package com.ekwe_hub.capitalstock_server.procurement.dto.response;

import java.util.UUID;

public record ManifestItemResponse(
    UUID id,
    UUID manifestId,
    String barcode,
    Integer expectedQuantity,
    Integer scannedQuantity
) {}
