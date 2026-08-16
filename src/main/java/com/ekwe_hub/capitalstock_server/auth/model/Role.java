package com.ekwe_hub.capitalstock_server.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    public enum RoleName {
        ROLE_SYSTEM_ADMIN,
        ROLE_MERCHANT_ADMIN,
        ROLE_STORE_STAFF,
        ROLE_SUPPLIER
    }
}
