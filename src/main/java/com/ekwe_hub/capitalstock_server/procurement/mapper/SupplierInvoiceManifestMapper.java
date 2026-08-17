package com.ekwe_hub.capitalstock_server.procurement.mapper;

import com.ekwe_hub.capitalstock_server.procurement.dto.response.ManifestItemResponse;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierInvoiceManifestResponse;
import com.ekwe_hub.capitalstock_server.procurement.model.ManifestItem;
import com.ekwe_hub.capitalstock_server.procurement.model.SupplierInvoiceManifest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SupplierInvoiceManifestMapper {

    public ManifestItemResponse toItemResponse(ManifestItem entity) {
        if (entity == null) return null;
        return new ManifestItemResponse(
                entity.getId(),
                entity.getManifestId(),
                entity.getBarcode(),
                entity.getExpectedQuantity(),
                entity.getScannedQuantity()
        );
    }

    public SupplierInvoiceManifestResponse toManifestResponse(SupplierInvoiceManifest entity) {
        if (entity == null) return null;
        List<ManifestItemResponse> itemResponses = entity.getManifestItems() != null ?
                entity.getManifestItems().stream().map(this::toItemResponse).toList() : List.of();

        return new SupplierInvoiceManifestResponse(
                entity.getId(),
                entity.getMerchantId(),
                entity.getSupplierId(),
                entity.getManifestNumber(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getReconciledAt(),
                itemResponses
        );
    }
}
