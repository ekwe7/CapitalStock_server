package com.ekwe_hub.capitalstock_server.admin.mapper;

import com.ekwe_hub.capitalstock_server.admin.dto.request.CreateSystemAdminRequest;
import com.ekwe_hub.capitalstock_server.admin.dto.response.SystemAdminResponse;
import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;
import org.springframework.stereotype.Component;

@Component
public class SystemAdminMapper {

    public SystemAdmin toEntity(CreateSystemAdminRequest request) {
        if (request == null) return null;
        return SystemAdmin.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(request.password())
                .role(request.role() != null ? request.role() : SystemAdmin.AdminRole.SYSTEM_ADMIN)
                .status(SystemAdmin.AdminStatus.ACTIVE)
                .build();
    }

    public SystemAdminResponse toResponse(SystemAdmin entity) {
        if (entity == null) return null;
        return new SystemAdminResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
