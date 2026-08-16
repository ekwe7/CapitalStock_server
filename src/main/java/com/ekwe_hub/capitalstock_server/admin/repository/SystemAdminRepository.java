package com.ekwe_hub.capitalstock_server.admin.repository;

import com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemAdminRepository extends JpaRepository<SystemAdmin, UUID> {
    Optional<SystemAdmin> findByEmail(String email);
    boolean existsByEmail(String email);
}
