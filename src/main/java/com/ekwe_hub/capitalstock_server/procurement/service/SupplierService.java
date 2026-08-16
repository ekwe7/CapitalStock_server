package com.ekwe_hub.capitalstock_server.procurement.service;

import com.ekwe_hub.capitalstock_server.procurement.dto.request.CreateSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.VetSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface SupplierService {
    SupplierResponse createSupplier(CreateSupplierRequest request);
    SupplierResponse vetSupplier(UUID supplierId, VetSupplierRequest request, UUID adminId);
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse getSupplierById(UUID id);
}
