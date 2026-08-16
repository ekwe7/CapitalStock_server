package com.ekwe_hub.capitalstock_server.merchant.model;

import com.ekwe_hub.capitalstock_server.auth.model.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "staff_email", nullable = false)
    private String staffEmail;

    @Column(name = "staff_full_name", nullable = false)
    private String staffFullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_role", nullable = false)
    private Role.RoleName assignedRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false)
    @Builder.Default
    private StaffInvitationStatus invitationStatus = StaffInvitationStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum StaffInvitationStatus {
        PENDING,
        ACCEPTED,
        DEACTIVATED
    }
}
