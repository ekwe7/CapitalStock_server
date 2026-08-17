package com.ekwe_hub.capitalstock_server.ledger.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class Sha256CryptographicHashGenerator {

    public String computePayloadHash(
            long sequenceNumber,
            UUID merchantId,
            UUID productId,
            String eventType,
            int quantityChange,
            int resultingBalance,
            String createdAtIsoString
    ) {
        String rawData = String.format(
                "%d|%s|%s|%s|%d|%d|%s",
                sequenceNumber,
                merchantId != null ? merchantId.toString() : "",
                productId != null ? productId.toString() : "",
                eventType,
                quantityChange,
                resultingBalance,
                createdAtIsoString
        );
        return applySha256(rawData);
    }

    public String computeSignatureHash(String previousHash, String recordPayloadHash) {
        String rawData = (previousHash != null ? previousHash : "0000000000000000000000000000000000000000000000000000000000000000") + "|" + recordPayloadHash;
        return applySha256(rawData);
    }

    private String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available in environment JVM", e);
        }
    }
}
