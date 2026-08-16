package com.ekwe_hub.capitalstock_server.procurement.mapper;

import com.ekwe_hub.capitalstock_server.procurement.dto.request.CreateSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;
import com.ekwe_hub.capitalstock_server.procurement.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(CreateSupplierRequest request) {
        if (request == null) return null;
        return Supplier.builder()
                .name(request.name())
                .code(request.code())
                .contactEmail(request.contactEmail())
                .status(Supplier.SupplierStatus.VETTED)
                .build();
    }

    public SupplierResponse toResponse(Supplier entity) {
        if (entity == null) return null;
        return new SupplierResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getContactEmail(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
