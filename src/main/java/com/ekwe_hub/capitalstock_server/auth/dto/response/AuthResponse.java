package com.ekwe_hub.capitalstock_server.auth.dto.response;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    String email,
    String role
) {
    public AuthResponse(String accessToken, String refreshToken, String email, String role) {
        this(accessToken, refreshToken, "Bearer", email, role);
    }
}
