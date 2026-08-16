package com.ekwe_hub.capitalstock_server.procurement.dto.request;

import com.ekwe_hub.capitalstock_server.procurement.model.Supplier;

public record VetSupplierRequest(
    Supplier.SupplierStatus status
) {}
