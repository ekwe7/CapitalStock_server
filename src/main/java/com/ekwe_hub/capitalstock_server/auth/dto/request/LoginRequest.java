package com.ekwe_hub.capitalstock_server.auth.dto.request;

public record LoginRequest(
    String email,
    String password
) {}
