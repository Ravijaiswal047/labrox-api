package com.labrox.reporting.service;

import com.labrox.reporting.domain.GeneratedReportArchive;
import com.labrox.reporting.domain.ReportTemplateHeader;
import com.labrox.reporting.dto.ReportVerificationResponse;
import com.labrox.reporting.repository.GeneratedReportArchiveRepository;
import com.labrox.reporting.repository.ReportTemplateHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportVerificationServiceTest {

    @Mock
    private GeneratedReportArchiveRepository archiveRepository;

    @Mock
    private ReportTemplateHeaderRepository headerRepository;

    private ReportVerificationService verificationService;

    @BeforeEach
    void setUp() {
        verificationService = new ReportVerificationService(archiveRepository, headerRepository);
    }

    @Test
    @DisplayName("Verify report token returns authentic=true with signature details when token exists")
    void testVerifyValidToken() {
        String token = "VRF-AUTH-VALID";
        GeneratedReportArchive archive = GeneratedReportArchive.builder()
                .id(1L)
                .testId(101L)
                .barcodeNo("S26308001001")
                .reportVersion(1)
                .verificationToken(token)
                .signatureStampHash("sha256-verified-hash-12345")
                .fileStoragePath("/storage/reports/S26308001001_v1.pdf")
                .fileSizeBytes(42500L)
                .renderedByUserId(1L)
                .renderedAt(LocalDateTime.now())
                .build();

        ReportTemplateHeader header = ReportTemplateHeader.builder()
                .centreName("LabRox Reference Laboratories")
                .active(true)
                .build();

        when(archiveRepository.findByVerificationToken(token)).thenReturn(Optional.of(archive));
        when(headerRepository.findFirstByActiveTrue()).thenReturn(Optional.of(header));

        ReportVerificationResponse response = verificationService.verifyReportToken(token);

        assertTrue(response.isAuthentic());
        assertEquals(token, response.getVerificationToken());
        assertEquals("S26308001001", response.getBarcodeNo());
        assertEquals("sha256-verified-hash-12345", response.getSignatureStampHash());
        assertEquals("LabRox Reference Laboratories", response.getIssuingAuthority());
    }

    @Test
    @DisplayName("Verify report token returns authentic=false for unknown or tampered token")
    void testVerifyInvalidToken() {
        String token = "UNKNOWN_FAKE_TOKEN";
        when(archiveRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        ReportVerificationResponse response = verificationService.verifyReportToken(token);

        assertFalse(response.isAuthentic());
        assertEquals(token, response.getVerificationToken());
        assertTrue(response.getMessage().contains("Invalid or expired"));
    }
}
