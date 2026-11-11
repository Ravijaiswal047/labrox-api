package com.labrox.iam;

import com.labrox.iam.service.PasswordEncoderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncoderServiceTest {

    private final PasswordEncoderService encoder = new PasswordEncoderService();

    @Test
    @DisplayName("Security: Argon2id password hashing and validation")
    void testArgon2idHashing() {
        String raw = "LabRoxSecurePassword2026!";
        String hash = encoder.hashPassword(raw);

        assertNotNull(hash);
        assertTrue(hash.contains("$"));
        assertTrue(encoder.verifyPassword(raw, hash, "ARGON2ID"));
        assertFalse(encoder.verifyPassword("WrongPassword", hash, "ARGON2ID"));
    }

    @Test
    @DisplayName("Migration: Legacy SHA-256 verification allows transparent password upgrade")
    void testLegacySha256Verification() {
        // Real SHA-256("Admin@123") in lowercase hex:
        String raw = "Admin@123";
        String legacyHash = "e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7";

        assertTrue(encoder.verifyPassword(raw, legacyHash, "SHA-256"));
        assertFalse(encoder.verifyPassword("WrongPassword", legacyHash, "SHA-256"));
    }
}
