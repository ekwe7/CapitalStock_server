package com.ekwe_hub.capitalstock_server.procurement.service;

import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import com.ekwe_hub.capitalstock_server.inventory.repository.ProductRepository;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.*;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.*;
import com.ekwe_hub.capitalstock_server.procurement.mapper.SupplierInvoiceManifestMapper;
import com.ekwe_hub.capitalstock_server.procurement.model.*;
import com.ekwe_hub.capitalstock_server.procurement.repository.*;
import com.ekwe_hub.capitalstock_server.procurement.service.impl.SupplierManifestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierManifestServiceTest {

    @Mock
    private SupplierInvoiceManifestRepository manifestRepository;

    @Mock
    private ManifestItemRepository manifestItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private SupplierInvoiceManifestMapper manifestMapper;

    @InjectMocks
    private SupplierManifestServiceImpl supplierManifestService;

    private UUID supplierId;
    private UUID merchantId;
    private SupplierInvoiceManifest manifest;
    private ManifestItem item;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();
        merchantId = UUID.randomUUID();

        manifest = SupplierInvoiceManifest.builder()
                .id(UUID.randomUUID())
                .supplierId(supplierId)
                .merchantId(merchantId)
                .manifestNumber("MNF-1001")
                .status(SupplierInvoiceManifest.ManifestStatus.PENDING_CHECKIN)
                .build();

        item = ManifestItem.builder()
                .id(UUID.randomUUID())
                .manifestId(manifest.getId())
                .barcode("88888888")
                .expectedQuantity(10)
                .scannedQuantity(0)
                .build();

        manifest.setManifestItems(List.of(item));
    }

    @Test
    void shouldSubmitSupplierInvoiceManifestSuccessfully() {
        SubmitSupplierManifestRequest request = new SubmitSupplierManifestRequest(
                merchantId, "MNF-1001", List.of(new SupplierManifestItemRequest("88888888", 10))
        );

        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        when(merchantRepository.existsById(merchantId)).thenReturn(true);
        when(manifestRepository.existsByManifestNumber("MNF-1001")).thenReturn(false);
        when(manifestRepository.save(any(SupplierInvoiceManifest.class))).thenReturn(manifest);

        SupplierInvoiceManifestResponse response = supplierManifestService.submitSupplierInvoiceManifest(supplierId, request);

        assertNotNull(response);
        assertEquals("MNF-1001", response.manifestNumber());
        assertEquals(SupplierInvoiceManifest.ManifestStatus.PENDING_CHECKIN, response.status());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldScanInwardBarcodeAndIncrementScannedQuantity() {
        UUID manifestId = manifest.getId();
        ScanInwardBarcodeRequest request = new ScanInwardBarcodeRequest("88888888", 1);

        when(manifestRepository.findById(manifestId)).thenReturn(Optional.of(manifest));
        when(manifestItemRepository.findByManifestIdAndBarcode(manifestId, "88888888")).thenReturn(Optional.of(item));

        SupplierInvoiceManifestResponse response = supplierManifestService.scanInwardDeliveryBarcode(manifestId, request, UUID.randomUUID());

        assertNotNull(response);
        assertEquals(1, item.getScannedQuantity());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldFinalize100PercentMatchedManifestAndElevateProductStockToVerifiedSupplierStock() {
        UUID manifestId = manifest.getId();
        item.setScannedQuantity(10); // 100% Match (10/10)

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .merchantId(merchantId)
                .barcode("88888888")
                .name("Samsung Galaxy S24")
                .availableQuantity(5)
                .verificationStatus(Product.StockVerificationStatus.UNVERIFIED_MANUAL)
                .build();

        when(manifestRepository.findById(manifestId)).thenReturn(Optional.of(manifest));
        when(manifestItemRepository.findByManifestId(manifestId)).thenReturn(List.of(item));
        when(manifestRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productRepository.findByMerchantIdAndBarcode(merchantId, "88888888")).thenReturn(Optional.of(product));

        SupplierInvoiceManifestResponse response = supplierManifestService.finalizeAndReconcileInwardDeliveryManifest(manifestId, UUID.randomUUID());

        assertNotNull(response);
        assertEquals(SupplierInvoiceManifest.ManifestStatus.RECONCILED, response.status());
        assertEquals(Product.StockVerificationStatus.VERIFIED_SUPPLIER_STOCK, product.getVerificationStatus());
        assertEquals(15, product.getAvailableQuantity()); // 5 baseline + 10 scanned
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
    }

    @Test
    void shouldFlagDiscrepancyWhenScannedQuantityDoesNotMatchExpectedQuantity() {
        UUID manifestId = manifest.getId();
        item.setScannedQuantity(7); // Short delivery (7/10)

        when(manifestRepository.findById(manifestId)).thenReturn(Optional.of(manifest));
        when(manifestItemRepository.findByManifestId(manifestId)).thenReturn(List.of(item));
        when(manifestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SupplierInvoiceManifestResponse response = supplierManifestService.finalizeAndReconcileInwardDeliveryManifest(manifestId, UUID.randomUUID());

        assertNotNull(response);
        assertEquals(SupplierInvoiceManifest.ManifestStatus.DISCREPANCY_REJECTED, response.status());
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
