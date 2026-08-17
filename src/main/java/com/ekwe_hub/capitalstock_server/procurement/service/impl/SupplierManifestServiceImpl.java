package com.ekwe_hub.capitalstock_server.procurement.service.impl;

import com.ekwe_hub.capitalstock_server.common.events.*;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import com.ekwe_hub.capitalstock_server.inventory.repository.ProductRepository;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.*;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.*;
import com.ekwe_hub.capitalstock_server.procurement.mapper.SupplierInvoiceManifestMapper;
import com.ekwe_hub.capitalstock_server.procurement.model.ManifestItem;
import com.ekwe_hub.capitalstock_server.procurement.model.SupplierInvoiceManifest;
import com.ekwe_hub.capitalstock_server.procurement.repository.ManifestItemRepository;
import com.ekwe_hub.capitalstock_server.procurement.repository.SupplierInvoiceManifestRepository;
import com.ekwe_hub.capitalstock_server.procurement.repository.SupplierRepository;
import com.ekwe_hub.capitalstock_server.procurement.service.SupplierManifestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierManifestServiceImpl implements SupplierManifestService {

    private final SupplierInvoiceManifestRepository manifestRepository;
    private final ManifestItemRepository manifestItemRepository;
    private final SupplierRepository supplierRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final SupplierInvoiceManifestMapper manifestMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SupplierInvoiceManifestResponse submitSupplierInvoiceManifest(UUID supplierId, SubmitSupplierManifestRequest request) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new IllegalArgumentException("Supplier record not found for ID: " + supplierId);
        }
        if (!merchantRepository.existsById(request.merchantId())) {
            throw new IllegalArgumentException("Merchant business record not found for ID: " + request.merchantId());
        }
        if (manifestRepository.existsByManifestNumber(request.manifestNumber())) {
            throw new IllegalArgumentException("Manifest number already exists: " + request.manifestNumber());
        }

        SupplierInvoiceManifest manifest = SupplierInvoiceManifest.builder()
                .supplierId(supplierId)
                .merchantId(request.merchantId())
                .manifestNumber(request.manifestNumber())
                .status(SupplierInvoiceManifest.ManifestStatus.PENDING_CHECKIN)
                .build();

        SupplierInvoiceManifest savedManifest = manifestRepository.save(manifest);

        List<ManifestItem> items = new ArrayList<>();
        if (request.manifestItems() != null) {
            for (SupplierManifestItemRequest itemReq : request.manifestItems()) {
                ManifestItem item = ManifestItem.builder()
                        .manifestId(savedManifest.getId())
                        .barcode(itemReq.barcode())
                        .expectedQuantity(itemReq.expectedQuantity())
                        .scannedQuantity(0)
                        .build();
                items.add(manifestItemRepository.save(item));
            }
        }
        savedManifest.setManifestItems(items);

        eventPublisher.publishEvent(new ManifestSubmittedEvent(
                savedManifest.getId(),
                savedManifest.getMerchantId(),
                savedManifest.getSupplierId(),
                savedManifest.getManifestNumber(),
                items.size(),
                LocalDateTime.now()
        ));

        return manifestMapper.toManifestResponse(savedManifest);
    }

    @Override
    @Transactional
    public SupplierInvoiceManifestResponse scanInwardDeliveryBarcode(UUID manifestId, ScanInwardBarcodeRequest request, UUID scannedByUserId) {
        SupplierInvoiceManifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier manifest not found for ID: " + manifestId));

        if (manifest.getStatus() == SupplierInvoiceManifest.ManifestStatus.RECONCILED ||
            manifest.getStatus() == SupplierInvoiceManifest.ManifestStatus.DISCREPANCY_REJECTED) {
            throw new IllegalStateException("Cannot scan barcode for finalized manifest with status: " + manifest.getStatus());
        }

        ManifestItem item = manifestItemRepository.findByManifestIdAndBarcode(manifestId, request.scannedBarcode())
                .orElseThrow(() -> new IllegalArgumentException("Scanned barcode: " + request.scannedBarcode() + " is not expected in manifest ID: " + manifestId));

        int increment = request.scannedQuantityIncrement() > 0 ? request.scannedQuantityIncrement() : 1;
        item.setScannedQuantity(item.getScannedQuantity() + increment);
        manifestItemRepository.save(item);

        if (manifest.getStatus() == SupplierInvoiceManifest.ManifestStatus.PENDING_CHECKIN) {
            manifest.setStatus(SupplierInvoiceManifest.ManifestStatus.IN_PROGRESS);
            manifestRepository.save(manifest);
        }

        eventPublisher.publishEvent(new ManifestItemScannedEvent(
                manifest.getId(),
                manifest.getMerchantId(),
                item.getBarcode(),
                item.getScannedQuantity(),
                item.getExpectedQuantity(),
                scannedByUserId,
                LocalDateTime.now()
        ));

        return manifestMapper.toManifestResponse(manifest);
    }

    @Override
    @Transactional
    public SupplierInvoiceManifestResponse finalizeAndReconcileInwardDeliveryManifest(UUID manifestId, UUID finalizedByUserId) {
        SupplierInvoiceManifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier manifest not found for ID: " + manifestId));

        List<ManifestItem> items = manifestItemRepository.findByManifestId(manifestId);

        boolean isAllItemsMatched = true;
        int totalVerifiedQuantity = 0;

        for (ManifestItem item : items) {
            totalVerifiedQuantity += item.getScannedQuantity();
            if (!item.getScannedQuantity().equals(item.getExpectedQuantity())) {
                isAllItemsMatched = false;
            }
        }

        if (isAllItemsMatched && !items.isEmpty()) {
            manifest.setStatus(SupplierInvoiceManifest.ManifestStatus.RECONCILED);
            manifest.setReconciledAt(LocalDateTime.now());
            SupplierInvoiceManifest reconciledManifest = manifestRepository.save(manifest);

            // Stock Elevation Trigger: Flipped to VERIFIED_SUPPLIER_STOCK & Available Stock Quantity Increased!
            for (ManifestItem item : items) {
                Optional<Product> productOpt = productRepository.findByMerchantIdAndBarcode(manifest.getMerchantId(), item.getBarcode());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    int previousAvailable = product.getAvailableQuantity();
                    int newAvailable = previousAvailable + item.getScannedQuantity();
                    product.setAvailableQuantity(newAvailable);
                    product.setVerificationStatus(Product.StockVerificationStatus.VERIFIED_SUPPLIER_STOCK);
                    productRepository.save(product);

                    eventPublisher.publishEvent(new StockMutationEvent(
                            manifest.getMerchantId(),
                            product.getId(),
                            "VERIFIED_SUPPLIER_INWARD_CHECKIN",
                            item.getScannedQuantity(),
                            newAvailable
                    ));
                }
            }

            eventPublisher.publishEvent(new ManifestVerifiedEvent(
                    reconciledManifest.getId(),
                    reconciledManifest.getMerchantId(),
                    reconciledManifest.getSupplierId(),
                    reconciledManifest.getManifestNumber(),
                    totalVerifiedQuantity,
                    finalizedByUserId,
                    LocalDateTime.now()
            ));

            return manifestMapper.toManifestResponse(reconciledManifest);
        } else {
            manifest.setStatus(SupplierInvoiceManifest.ManifestStatus.DISCREPANCY_REJECTED);
            SupplierInvoiceManifest rejectedManifest = manifestRepository.save(manifest);

            eventPublisher.publishEvent(new ManifestDiscrepancyFlaggedEvent(
                    rejectedManifest.getId(),
                    rejectedManifest.getMerchantId(),
                    rejectedManifest.getSupplierId(),
                    rejectedManifest.getManifestNumber(),
                    "Discrepancy detected during physical double-blind inward check-in scan.",
                    finalizedByUserId,
                    LocalDateTime.now()
            ));

            return manifestMapper.toManifestResponse(rejectedManifest);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ManifestCheckinProgressResponse retrieveManifestCheckinProgress(UUID manifestId) {
        SupplierInvoiceManifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier manifest not found for ID: " + manifestId));

        List<ManifestItem> items = manifestItemRepository.findByManifestId(manifestId);

        int totalExpected = items.stream().mapToInt(ManifestItem::getExpectedQuantity).sum();
        int totalScanned = items.stream().mapToInt(ManifestItem::getScannedQuantity).sum();

        double progress = totalExpected > 0 ? ((double) totalScanned / totalExpected) * 100.0 : 0.0;

        return new ManifestCheckinProgressResponse(
                manifest.getId(),
                manifest.getManifestNumber(),
                manifest.getStatus(),
                totalExpected,
                totalScanned,
                progress
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierInvoiceManifestResponse> retrieveManifestsByMerchantId(UUID merchantId) {
        return manifestRepository.findByMerchantId(merchantId)
                .stream()
                .map(manifestMapper::toManifestResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierInvoiceManifestResponse> retrieveManifestsBySupplierId(UUID supplierId) {
        return manifestRepository.findBySupplierId(supplierId)
                .stream()
                .map(manifestMapper::toManifestResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierInvoiceManifestResponse retrieveManifestById(UUID manifestId) {
        SupplierInvoiceManifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier manifest not found for ID: " + manifestId));
        return manifestMapper.toManifestResponse(manifest);
    }
}
