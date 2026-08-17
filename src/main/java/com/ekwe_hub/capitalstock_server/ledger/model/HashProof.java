package com.ekwe_hub.capitalstock_server.ledger.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashProof {

    @Column(name = "record_payload_hash", nullable = false, length = 64)
    private String recordPayloadHash;

    @Column(name = "previous_hash", nullable = false, length = 64)
    private String previousHash;

    @Column(name = "current_signature_hash", nullable = false, length = 64)
    private String currentSignatureHash;
}
