package com.ekwe_hub.capitalstock_server.merchant.service;

import com.ekwe_hub.capitalstock_server.admin.dto.request.OnboardMerchantRequest;
import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;

import java.util.List;
import java.util.UUID;

public interface MerchantService {
    MerchantResponse onboardMerchant(OnboardMerchantRequest request);
    List<MerchantResponse> getAllMerchants();
    MerchantResponse getMerchantById(UUID id);
    MerchantResponse updateMerchantStatus(UUID id, Merchant.MerchantStatus newStatus);
}
