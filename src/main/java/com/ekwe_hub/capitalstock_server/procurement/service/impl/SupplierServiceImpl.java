package com.ekwe_hub.capitalstock_server.procurement.service.impl;

import com.ekwe_hub.capitalstock_server.common.events.SupplierVettedEvent;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.CreateSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.VetSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;
import com.ekwe_hub.capitalstock_server.procurement.mapper.SupplierMapper;
import com.ekwe_hub.capitalstock_server.procurement.model.Supplier;
import com.ekwe_hub.capitalstock_server.procurement.repository.SupplierRepository;
import com.ekwe_hub.capitalstock_server.procurement.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        if (supplierRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Supplier code already exists: " + request.code());
        }

        Supplier supplier = supplierMapper.toEntity(request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierResponse vetSupplier(UUID supplierId, VetSupplierRequest request, UUID adminId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));

        supplier.setStatus(request.status());
        Supplier updatedSupplier = supplierRepository.save(supplier);

        // Publish SupplierVettedEvent
        eventPublisher.publishEvent(new SupplierVettedEvent(
                updatedSupplier.getId(),
                updatedSupplier.getCode(),
                updatedSupplier.getStatus().name(),
                adminId,
                LocalDateTime.now()
        ));

        return supplierMapper.toResponse(updatedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + id));
        return supplierMapper.toResponse(supplier);
    }
}
