package com.ekwe_hub.capitalstock_server.procurement.dto.request;

public record SupplierManifestItemRequest(
    String barcode,
    Integer expectedQuantity
) {}
