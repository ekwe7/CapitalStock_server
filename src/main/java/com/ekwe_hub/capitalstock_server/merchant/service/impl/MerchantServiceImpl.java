package com.ekwe_hub.capitalstock_server.merchant.service.impl;

import com.ekwe_hub.capitalstock_server.admin.dto.request.OnboardMerchantRequest;
import com.ekwe_hub.capitalstock_server.admin.dto.response.MerchantResponse;
import com.ekwe_hub.capitalstock_server.merchant.mapper.MerchantMapper;
import com.ekwe_hub.capitalstock_server.merchant.model.Merchant;
import com.ekwe_hub.capitalstock_server.merchant.repository.MerchantRepository;
import com.ekwe_hub.capitalstock_server.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantResponse onboardMerchant(OnboardMerchantRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Merchant email already exists: " + request.email());
        }

        Merchant merchant = merchantMapper.toEntity(request);
        return merchantMapper.toResponse(merchantRepository.save(merchant));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll()
                .stream()
                .map(merchantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantResponse getMerchantById(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found with ID: " + id));
        return merchantMapper.toResponse(merchant);
    }

    @Override
    @Transactional
    public MerchantResponse updateMerchantStatus(UUID id, Merchant.MerchantStatus newStatus) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found with ID: " + id));

        merchant.setStatus(newStatus);
        return merchantMapper.toResponse(merchantRepository.save(merchant));
    }
}
