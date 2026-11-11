package com.labrox.approval.service;

import com.labrox.approval.domain.ElectronicSignature;
import com.labrox.approval.repository.ElectronicSignatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElectronicSignatureServiceTest {

    @Mock
    private ElectronicSignatureRepository signatureRepository;

    @InjectMocks
    private ElectronicSignatureService signatureService;

    @Test
    @DisplayName("Generate 21 CFR Part 11 compliant 64-character SHA-256 digital signature stamp")
    void testGenerateSignatureHash() {
        Long testId = 5001L;
        Integer signerId = 12;
        String regNo = "MCI-78491";
        Instant now = Instant.now();
        String meaning = "I have reviewed and approved these diagnostic test results.";

        String hash = signatureService.generateSignatureHash(testId, signerId, regNo, now, meaning);

        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hash must be exactly 64 hex characters");
    }

    @Test
    @DisplayName("Dual-factor PIN verification against SHA-256 hash")
    void testVerifySignerPin() throws Exception {
        String pin = "123456";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String expectedHash = HexFormat.of().formatHex(digest.digest(pin.getBytes(StandardCharsets.UTF_8)));

        assertTrue(signatureService.verifySignerPin("123456", expectedHash));
        assertFalse(signatureService.verifySignerPin("999999", expectedHash));
    }

    @Test
    @DisplayName("Sign report creates valid electronic signature with audit details")
    void testSignReport() {
        when(signatureRepository.save(any(ElectronicSignature.class))).thenAnswer(i -> i.getArgument(0));

        ElectronicSignature sig = signatureService.signReport(
                1,
                5001L,
                12,
                "Dr. Ramesh Chandra, MD",
                "MCI-78491",
                "Senior Pathologist",
                "Reviewed and verified"
        );

        assertNotNull(sig);
        assertEquals("Dr. Ramesh Chandra, MD", sig.getSignerName());
        assertEquals("MCI-78491", sig.getRegistrationNumber());
        assertTrue(sig.isValid());
        assertNotNull(sig.getSignatureHash());
        assertEquals(64, sig.getSignatureHash().length());
    }
}
