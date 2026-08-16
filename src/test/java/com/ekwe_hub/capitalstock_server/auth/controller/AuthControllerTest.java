package com.ekwe_hub.capitalstock_server.auth.controller;

import com.ekwe_hub.capitalstock_server.auth.dto.request.LoginRequest;
import com.ekwe_hub.capitalstock_server.auth.dto.response.AuthResponse;
import com.ekwe_hub.capitalstock_server.auth.dto.response.UserProfileResponse;
import com.ekwe_hub.capitalstock_server.auth.service.AuthService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("admin@veritastock.com", "password123");
        AuthResponse response = new AuthResponse("access_jwt_token", "refresh_jwt_token", "admin@veritastock.com", "ROLE_SYSTEM_ADMIN");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_jwt_token"))
                .andExpect(jsonPath("$.role").value("ROLE_SYSTEM_ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin@veritastock.com")
    void shouldReturnCurrentUserProfile() throws Exception {
        UserProfileResponse profile = new UserProfileResponse(
                UUID.randomUUID(), "admin@veritastock.com", "Root Admin", null, List.of("ROLE_SYSTEM_ADMIN"), "ACTIVE"
        );

        when(authService.getCurrentUserProfile("admin@veritastock.com")).thenReturn(profile);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@veritastock.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_SYSTEM_ADMIN"));
    }
}
