package com.ekwe_hub.capitalstock_server.procurement.dto.request;

public record CreateSupplierRequest(
    String name,
    String code,
    String contactEmail
) {}
