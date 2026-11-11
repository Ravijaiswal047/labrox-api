package com.labrox.iam.service;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordEncoderService {

    private static final int ITERATIONS = 3;
    private static final int MEMORY_KB = 65536; // 64 MB
    private static final int PARALLELISM = 1;
    private static final int HASH_LENGTH = 32;

    public String hashPassword(String rawPassword) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_KB)
                .withParallelism(PARALLELISM)
                .build();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);

        byte[] result = new byte[HASH_LENGTH];
        generator.generateBytes(rawPassword.getBytes(StandardCharsets.UTF_8), result, 0, result.length);

        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(result);
    }

    public boolean verifyPassword(String rawPassword, String storedHash, String algo) {
        if ("SHA-256".equalsIgnoreCase(algo) || "SHA256".equalsIgnoreCase(algo)) {
            return verifyLegacySha256(rawPassword, storedHash);
        }

        // Default to Argon2id
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withIterations(ITERATIONS)
                    .withMemoryAsKB(MEMORY_KB)
                    .withParallelism(PARALLELISM)
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] actualHash = new byte[HASH_LENGTH];
            generator.generateBytes(rawPassword.getBytes(StandardCharsets.UTF_8), actualHash, 0, actualHash.length);

            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyLegacySha256(String rawPassword, String expectedHexHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equalsIgnoreCase(expectedHexHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing", e);
        }
    }
}

