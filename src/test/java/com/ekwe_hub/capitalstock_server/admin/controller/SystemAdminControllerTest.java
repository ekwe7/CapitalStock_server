package com.ekwe_hub.capitalstock_server.admin.controller;

import com.ekwe_hub.capitalstock_server.admin.dto.request.*;
import com.ekwe_hub.capitalstock_server.admin.dto.response.*;
import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;
import com.ekwe_hub.capitalstock_server.admin.service.SystemAdminService;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import com.ekwe_hub.capitalstock_server.procurement.dto.request.VetSupplierRequest;
import com.ekwe_hub.capitalstock_server.procurement.dto.response.SupplierResponse;
import com.ekwe_hub.capitalstock_server.procurement.model.Supplier;
import com.ekwe_hub.capitalstock_server.security.jwt.JwtTokenProvider;
import com.ekwe_hub.capitalstock_server.security.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SystemAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class SystemAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SystemAdminService systemAdminService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(authorities = "ROLE_SYSTEM_ADMIN")
    void shouldCreateSystemAdmin() throws Exception {
        CreateSystemAdminRequest request = new CreateSystemAdminRequest(
                "Super Admin", "admin@veritastock.com", "password123", SystemAdmin.AdminRole.SUPER_ADMIN
        );
        SystemAdminResponse response = new SystemAdminResponse(
                UUID.randomUUID(), "Super Admin", "admin@veritastock.com", SystemAdmin.AdminRole.SUPER_ADMIN, SystemAdmin.AdminStatus.ACTIVE, LocalDateTime.now()
        );

        when(systemAdminService.createSystemAdmin(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@veritastock.com"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_SYSTEM_ADMIN")
    void shouldOnboardMerchant() throws Exception {
        OnboardMerchantRequest request = new OnboardMerchantRequest("Computer Village Hub", "hub@computervillage.com", "+2348111111111");
        MerchantResponse response = new MerchantResponse(
                UUID.randomUUID(), "Computer Village Hub", "hub@computervillage.com", "+2348111111111", Merchant.MerchantStatus.ACTIVE, LocalDateTime.now()
        );

        when(systemAdminService.onboardMerchant(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Computer Village Hub"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_SYSTEM_ADMIN")
    void shouldVetSupplier() throws Exception {
        UUID supplierId = UUID.randomUUID();
        VetSupplierRequest request = new VetSupplierRequest(Supplier.SupplierStatus.VETTED);
        SupplierResponse response = new SupplierResponse(supplierId, "Dell Direct", "SUP-DELL", "dell@supplier.com", Supplier.SupplierStatus.VETTED, LocalDateTime.now());

        when(systemAdminService.vetSupplier(eq(supplierId), any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/admin/suppliers/" + supplierId + "/vet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VETTED"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_SYSTEM_ADMIN")
    void shouldFetchGlobalAuditHealth() throws Exception {
        GlobalAuditHealthResponse health = new GlobalAuditHealthResponse(100L, 0, "SECURE", List.of(), LocalDateTime.now());
        when(systemAdminService.getGlobalAuditHealth()).thenReturn(health);

        mockMvc.perform(get("/api/admin/audits/global-health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallIntegrityStatus").value("SECURE"));
    }
}
