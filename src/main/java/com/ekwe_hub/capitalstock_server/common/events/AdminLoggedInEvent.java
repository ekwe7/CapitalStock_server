package com.ekwe_hub.capitalstock_server.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminLoggedInEvent(
    UUID adminUserId,
    String email,
    String ipAddress,
    LocalDateTime timestamp
) {}
