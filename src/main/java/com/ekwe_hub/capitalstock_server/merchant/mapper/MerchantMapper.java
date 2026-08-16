package com.ekwe_hub.capitalstock_server.merchant.mapper;

import com.ekwe_hub.capitalstock_server.admin.dto.request.OnboardMerchantRequest;
import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import org.springframework.stereotype.Component;

@Component
public class MerchantMapper {

    public Merchant toEntity(OnboardMerchantRequest request) {
        if (request == null) return null;
        return Merchant.builder()
                .name(request.name())
                .email(request.email())
                .businessPhone(request.businessPhone())
                .status(Merchant.MerchantStatus.ACTIVE)
                .build();
    }

    public MerchantResponse toResponse(Merchant entity) {
        if (entity == null) return null;
        return new MerchantResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getBusinessPhone(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
