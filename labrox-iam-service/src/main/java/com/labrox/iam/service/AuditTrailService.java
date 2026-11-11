package com.labrox.iam.service;

import com.labrox.iam.domain.AuditTrailEvent;
import com.labrox.iam.dto.AuditChainVerificationResponse;
import com.labrox.iam.dto.AuditLogRequest;
import com.labrox.iam.repository.AuditTrailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuditTrailService {

    private static final String GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";
    private final AuditTrailRepository auditRepository;

    public AuditTrailService(AuditTrailRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
    public AuditTrailEvent recordAudit(AuditLogRequest req) {
        // 1. Fetch previous hash for this entity chain
        Optional<AuditTrailEvent> latest = auditRepository.findLatestByEntity(
                req.getCompanyId(), req.getEntityName(), req.getEntityId());
        String previousHash = latest.map(AuditTrailEvent::getCurrentHash).orElse(GENESIS_HASH);

        // 2. Compute current hash: SHA256(previousHash | companyId | entityName | entityId | action | prevVal | newVal | userId | time)
        Instant now = Instant.now();
        String currentHash = computeHash(previousHash, req.getCompanyId(), req.getEntityName(),
                req.getEntityId(), req.getAction(), req.getPreviousValue(), req.getNewValue(),
                req.getUserId(), now, req.getUserIp());

        // 3. Persist immutable record
        AuditTrailEvent event = AuditTrailEvent.builder()
                .companyId(req.getCompanyId())
                .entityName(req.getEntityName())
                .entityId(req.getEntityId())
                .action(req.getAction())
                .previousValue(req.getPreviousValue())
                .newValue(req.getNewValue())
                .changeReason(req.getChangeReason())
                .userId(req.getUserId())
                .userIp(req.getUserIp())
                .timestamp(now)
                .previousHash(previousHash)
                .currentHash(currentHash)
                .signature(req.getSignature())
                .build();

        return auditRepository.save(event);
    }

    /**
     * Verifies the complete cryptographic hash chain for an entity under 21 CFR Part 11.
     */
    @Transactional(readOnly = true)
    public AuditChainVerificationResponse verifyChain(Integer companyId, String entityName, String entityId) {
        List<AuditTrailEvent> chain = auditRepository.findByCompanyIdAndEntityNameAndEntityIdOrderByIdAsc(
                companyId, entityName, entityId);

        if (chain.isEmpty()) {
            return AuditChainVerificationResponse.builder()
                    .valid(true)
                    .totalRecordsVerified(0)
                    .message("No records found for entity; chain is clean.")
                    .build();
        }

        String expectedPreviousHash = GENESIS_HASH;
        for (AuditTrailEvent event : chain) {
            // Check previous hash link
            if (!event.getPreviousHash().equals(expectedPreviousHash)) {
                return AuditChainVerificationResponse.builder()
                        .valid(false)
                        .totalRecordsVerified(chain.indexOf(event))
                        .corruptedRecordId(event.getId())
                        .message("Chain broken! Previous hash mismatch at record ID " + event.getId())
                        .build();
            }

            // Recompute current hash to verify data integrity
            String recalculatedHash = computeHash(event.getPreviousHash(), event.getCompanyId(),
                    event.getEntityName(), event.getEntityId(), event.getAction(), event.getPreviousValue(),
                    event.getNewValue(), event.getUserId(), event.getTimestamp(), event.getUserIp());

            if (!event.getCurrentHash().equalsIgnoreCase(recalculatedHash)) {
                return AuditChainVerificationResponse.builder()
                        .valid(false)
                        .totalRecordsVerified(chain.indexOf(event))
                        .corruptedRecordId(event.getId())
                        .message("Data tampering detected! Record ID " + event.getId() + " payload does not match cryptographic hash.")
                        .build();
            }

            expectedPreviousHash = event.getCurrentHash();
        }

        return AuditChainVerificationResponse.builder()
                .valid(true)
                .totalRecordsVerified(chain.size())
                .message("21 CFR Part 11 Audit Chain verified successfully. 100% cryptographic integrity.")
                .build();
    }

    private String computeHash(String prevHash, Integer companyId, String entityName, String entityId,
                               String action, String prevVal, String newVal, Integer userId, Instant time, String userIp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String payload = String.format("%s|%d|%s|%s|%s|%s|%s|%d|%s|%s",
                    prevHash, companyId, entityName, entityId, action,
                    prevVal != null ? prevVal : "",
                    newVal != null ? newVal : "",
                    userId, time.toString(),
                    userIp != null ? userIp : "");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }
}

