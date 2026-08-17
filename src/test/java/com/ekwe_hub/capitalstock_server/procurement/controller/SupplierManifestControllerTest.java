package com.ekwe_hub.capitalstock_server.procurement.controller;

import com.ekwe_hub.capitalstock_server.procurement.dto.request.*;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.*;
import com.ekwe_hub.capitalstock_server.procurement.model.SupplierInvoiceManifest;
import com.ekwe_hub.capitalstock_server.procurement.service.SupplierManifestService;
import com.ekwe_hub.capitalstock_server.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupplierManifestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierManifestService supplierManifestService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(authorities = "ROLE_SUPPLIER")
    void shouldSubmitSupplierManifest() throws Exception {
        UUID supplierId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();

        SubmitSupplierManifestRequest request = new SubmitSupplierManifestRequest(
                merchantId, "MNF-9999", List.of(new SupplierManifestItemRequest("123456", 5))
        );

        SupplierInvoiceManifestResponse response = new SupplierInvoiceManifestResponse(
                UUID.randomUUID(), merchantId, supplierId, "MNF-9999",
                SupplierInvoiceManifest.ManifestStatus.PENDING_CHECKIN,
                LocalDateTime.now(), null, List.of()
        );

        when(supplierManifestService.submitSupplierInvoiceManifest(eq(supplierId), any())).thenReturn(response);

        mockMvc.perform(post("/api/suppliers/" + supplierId + "/manifests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.manifestNumber").value("MNF-9999"))
                .andExpect(jsonPath("$.status").value("PENDING_CHECKIN"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_STORE_STAFF")
    void shouldScanInwardBarcode() throws Exception {
        UUID manifestId = UUID.randomUUID();
        ScanInwardBarcodeRequest request = new ScanInwardBarcodeRequest("123456", 1);

        SupplierInvoiceManifestResponse response = new SupplierInvoiceManifestResponse(
                manifestId, UUID.randomUUID(), UUID.randomUUID(), "MNF-9999",
                SupplierInvoiceManifest.ManifestStatus.IN_PROGRESS,
                LocalDateTime.now(), null, List.of()
        );

        when(supplierManifestService.scanInwardDeliveryBarcode(eq(manifestId), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/manifests/" + manifestId + "/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_STORE_STAFF")
    void shouldFinalizeManifestCheckin() throws Exception {
        UUID manifestId = UUID.randomUUID();

        SupplierInvoiceManifestResponse response = new SupplierInvoiceManifestResponse(
                manifestId, UUID.randomUUID(), UUID.randomUUID(), "MNF-9999",
                SupplierInvoiceManifest.ManifestStatus.RECONCILED,
                LocalDateTime.now(), LocalDateTime.now(), List.of()
        );

        when(supplierManifestService.finalizeAndReconcileInwardDeliveryManifest(eq(manifestId), any())).thenReturn(response);

        mockMvc.perform(post("/api/manifests/" + manifestId + "/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECONCILED"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_STORE_STAFF")
    void shouldRetrieveManifestCheckinProgress() throws Exception {
        UUID manifestId = UUID.randomUUID();

        ManifestCheckinProgressResponse response = new ManifestCheckinProgressResponse(
                manifestId, "MNF-9999", SupplierInvoiceManifest.ManifestStatus.IN_PROGRESS, 10, 8, 80.0
        );

        when(supplierManifestService.retrieveManifestCheckinProgress(manifestId)).thenReturn(response);

        mockMvc.perform(get("/api/manifests/" + manifestId + "/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manifestNumber").value("MNF-9999"))
                .andExpect(jsonPath("$.checkinCompletionPercentageProgress").value(80.0));
    }
}
