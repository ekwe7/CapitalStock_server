package com.ekwe_hub.capitalstock_server.merchant.repository;

import com.ekwe_hub.capitalstock_server.merchant.model.StaffInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffInvitationRepository extends JpaRepository<StaffInvitation, UUID> {
    List<StaffInvitation> findByMerchantId(UUID merchantId);
    Optional<StaffInvitation> findByMerchantIdAndStaffEmail(UUID merchantId, String staffEmail);
    boolean existsByMerchantIdAndStaffEmail(UUID merchantId, String staffEmail);
}
