package com.ekwe_hub.capitalstock_server.procurement.repository;

import com.ekwe_hub.capitalstock_server.procurement.model.ManifestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManifestItemRepository extends JpaRepository<ManifestItem, UUID> {
    List<ManifestItem> findByManifestId(UUID manifestId);
    Optional<ManifestItem> findByManifestIdAndBarcode(UUID manifestId, String barcode);
}
