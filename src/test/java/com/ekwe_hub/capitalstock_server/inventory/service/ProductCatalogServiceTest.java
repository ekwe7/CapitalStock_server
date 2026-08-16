package com.ekwe_hub.capitalstock_server.inventory.service;

import com.ekwe_hub.capitalstock_server.inventory.dto.request.*;
import com.ekwe_hub.capitalstock_server.inventory.dto.response.ProductResponse;
import com.ekwe_hub.capitalstock_server.inventory.mapper.ProductMapper;
import com.ekwe_hub.capitalstock_server.inventory.model.Product;
import com.ekwe_hub.capitalstock_server.inventory.repository.ProductRepository;
import com.ekwe_hub.capitalstock_server.inventory.service.impl.ProductCatalogServiceImpl;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private ProductMapper productMapper;

    @InjectMocks
    private ProductCatalogServiceImpl productCatalogService;

    private UUID merchantId;
    private Product product;

    @BeforeEach
    void setUp() {
        merchantId = UUID.randomUUID();
        product = Product.builder()
                .id(UUID.randomUUID())
                .merchantId(merchantId)
                .barcode("1234567890")
                .name("MacBook Pro M3")
                .sku("SKU-MBP-M3")
                .costPrice(BigDecimal.valueOf(1200000))
                .sellingPrice(BigDecimal.valueOf(1500000))
                .price(BigDecimal.valueOf(1500000))
                .availableQuantity(10)
                .reservedQuantity(0)
                .advanceRatePercentage(BigDecimal.valueOf(70.0))
                .verificationStatus(Product.StockVerificationStatus.UNVERIFIED_MANUAL)
                .build();
    }

    @Test
    void shouldCreateProductWithUnverifiedManualStatus() {
        CreateProductRequest request = new CreateProductRequest(
                UUID.randomUUID(), "1234567890", "MacBook Pro M3", "SKU-MBP-M3",
                BigDecimal.valueOf(1200000), BigDecimal.valueOf(1500000), 10, BigDecimal.valueOf(70.0)
        );

        when(merchantRepository.existsById(merchantId)).thenReturn(true);
        when(productRepository.existsByMerchantIdAndBarcode(merchantId, request.barcode())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productCatalogService.createProductInMerchantCatalog(merchantId, request, UUID.randomUUID());

        assertNotNull(response);
        assertEquals("1234567890", response.barcode());
        assertEquals(Product.StockVerificationStatus.UNVERIFIED_MANUAL, response.verificationStatus());
        verify(eventPublisher, times(2)).publishEvent(any(Object.class)); // ProductCreatedEvent + StockMutationEvent
    }

    @Test
    void shouldPerformManualUnverifiedStockAdjustment() {
        UUID productId = product.getId();
        PerformManualStockAdjustmentRequest request = new PerformManualStockAdjustmentRequest(5, "Manual restock baseline");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponse response = productCatalogService.performManualUnverifiedStockAdjustment(merchantId, productId, request, UUID.randomUUID());

        assertNotNull(response);
        assertEquals(15, response.availableQuantity());
        assertEquals(Product.StockVerificationStatus.UNVERIFIED_MANUAL, response.verificationStatus());
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
    }
}
