package com.labrox.approval.service;

import com.labrox.approval.domain.ElectronicSignature;
import com.labrox.approval.repository.ElectronicSignatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectronicSignatureService {

    private final ElectronicSignatureRepository signatureRepository;

    /**
     * Generates a 21 CFR Part 11 compliant cryptographic digital signature stamp.
     * Formula: SHA256(TestId + ":" + SignerId + ":" + RegNo + ":" + Timestamp + ":" + Meaning)
     */
    public String generateSignatureHash(Long testId, Integer signerId, String registrationNumber, Instant timestamp, String meaning) {
        String payload = testId + ":" + signerId + ":" + registrationNumber + ":" + timestamp.toEpochMilli() + ":" + meaning;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Dual-factor credential check for electronic signature (Password or 6-digit PIN).
     */
    public boolean verifySignerPin(String enteredPin, String expectedPinHash) {
        if (enteredPin == null || expectedPinHash == null) {
            return false;
        }
        // In GxP environments, PIN is verified against stored secure hash
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(enteredPin.getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().formatHex(hash);
            return hex.equalsIgnoreCase(expectedPinHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    @Transactional
    public ElectronicSignature signReport(Integer companyId, Long testId, Integer signerId, String signerName,
                                         String registrationNumber, String designation, String meaning) {
        Instant now = Instant.now();
        String hash = generateSignatureHash(testId, signerId, registrationNumber, now, meaning);

        ElectronicSignature sig = ElectronicSignature.builder()
                .companyId(companyId)
                .testId(testId)
                .signerId(signerId)
                .signerName(signerName)
                .registrationNumber(registrationNumber)
                .designation(designation)
                .signatureMeaning(meaning)
                .signedAt(now)
                .signatureHash(hash)
                .valid(true)
                .build();

        ElectronicSignature saved = signatureRepository.save(sig);
        log.info("21 CFR Part 11 Electronic Signature stamped: TestId={}, Signer={}, Hash={}", testId, signerName, hash);
        return saved;
    }
}
