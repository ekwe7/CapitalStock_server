package com.ekwe_hub.capitalstock_server.auth.dto.response;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
    UUID id,
    String email,
    String fullName,
    UUID merchantId,
    List<String> roles,
    String status
) {}
