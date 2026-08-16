package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierVettedEvent(
    UUID supplierId,
    String supplierCode,
    String status, // e.g. "VETTED", "REVOKED", "SUSPENDED"
    UUID vettedByAdminId,
    LocalDateTime timestamp
) {}
