package com.labrox.common.audit;

import lombok.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashChainedAuditEntry {
    private Long auditId;
    private Integer companyId;
    private String entityName;
    private String entityId;
    private String action; // CREATE, UPDATE, APPROVE, AMEND
    private String previousValue;
    private String newValue;
    private Integer userId;
    private String userIp;
    private Instant timestamp;
    private String previousHash;
    private String currentHash;

    public String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = String.format("%s|%d|%s|%s|%s|%s|%s|%d|%s|%s",
                    previousHash != null ? previousHash : "ROOT_GENESIS",
                    companyId,
                    entityName,
                    entityId,
                    action,
                    previousValue != null ? previousValue : "",
                    newValue != null ? newValue : "",
                    userId,
                    timestamp.toString(),
                    userIp != null ? userIp : "");
            byte[] encodedhash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing", e);
        }
    }
}
