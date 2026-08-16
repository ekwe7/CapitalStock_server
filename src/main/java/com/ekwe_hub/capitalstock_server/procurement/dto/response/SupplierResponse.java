package com.ekwe_hub.capitalstock_server.procurement.dto.response;

import com.ekwe_hub.capitalstock_server.procurement.model.Supplier;
import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierResponse(
    UUID id,
    String name,
    String code,
    String contactEmail,
    Supplier.SupplierStatus status,
    LocalDateTime createdAt
) {}
