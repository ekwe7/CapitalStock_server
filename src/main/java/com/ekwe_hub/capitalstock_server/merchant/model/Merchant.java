package com.ekwe_hub.capitalstock_server.merchant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "business_phone")
    private String businessPhone;

    @Column(name = "registration_number_rc_number")
    private String registrationNumberRcNumber;

    @Column(name = "flutterwave_subaccount_code")
    private String flutterwaveSubaccountCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum MerchantStatus {
        PENDING_ONBOARDING,
        ACTIVE,
        SUSPENDED,
        DEACTIVATED
    }
}
