package com.ekwe_hub.capitalstock_server.ledger.controller;

import com.ekwe_hub.capitalstock_server.ledger.dto.response.*;
import com.ekwe_hub.capitalstock_server.ledger.service.CryptographicLedgerService;
import com.ekwe_hub.capitalstock_server.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptographicLedgerService cryptographicLedgerService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(authorities = "ROLE_SYSTEM_ADMIN")
    void shouldVerifyMerchantLedgerCryptographicIntegrity() throws Exception {
        UUID merchantId = UUID.randomUUID();

        LedgerAuditVerificationResponse response = new LedgerAuditVerificationResponse(
                merchantId, true, 10, 0,
                "Ledger integrity audit passed 100% cleanly.", LocalDateTime.now()
        );

        when(cryptographicLedgerService.verifyMerchantLedgerCryptographicIntegrity(merchantId)).thenReturn(response);

        mockMvc.perform(get("/api/ledger/merchants/" + merchantId + "/audit-verification"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLedgerIntegrityVerifiedIntact").value(true))
                .andExpect(jsonPath("$.totalBlocksAuditedCount").value(10));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MERCHANT_ADMIN")
    void shouldTriggerMerchantLedgerAudit() throws Exception {
        UUID merchantId = UUID.randomUUID();

        LedgerAuditVerificationResponse response = new LedgerAuditVerificationResponse(
                merchantId, true, 5, 0,
                "Ledger integrity audit passed 100% cleanly.", LocalDateTime.now()
        );

        when(cryptographicLedgerService.verifyMerchantLedgerCryptographicIntegrity(merchantId)).thenReturn(response);

        mockMvc.perform(post("/api/ledger/merchants/" + merchantId + "/trigger-audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLedgerIntegrityVerifiedIntact").value(true));
    }

    @Test
    @WithMockUser(authorities = "ROLE_SYSTEM_ADMIN")
    void shouldRetrieveLedgerBlocksForMerchant() throws Exception {
        UUID merchantId = UUID.randomUUID();

        LedgerBlockResponse block = new LedgerBlockResponse(
                UUID.randomUUID(), merchantId, 1L, UUID.randomUUID(),
                "SUPPLIER_CHECKIN", 10, 10, "payloadHash123", "000000", "sigHash123", LocalDateTime.now()
        );

        when(cryptographicLedgerService.retrieveLedgerBlocksForMerchant(merchantId)).thenReturn(List.of(block));

        mockMvc.perform(get("/api/ledger/merchants/" + merchantId + "/blocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sequenceNumber").value(1))
                .andExpect(jsonPath("$[0].eventType").value("SUPPLIER_CHECKIN"));
    }
}
