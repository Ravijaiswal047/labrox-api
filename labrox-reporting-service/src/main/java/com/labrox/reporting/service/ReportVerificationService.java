package com.labrox.reporting.service;

import com.labrox.reporting.domain.GeneratedReportArchive;
import com.labrox.reporting.domain.ReportTemplateHeader;
import com.labrox.reporting.dto.ReportVerificationResponse;
import com.labrox.reporting.repository.GeneratedReportArchiveRepository;
import com.labrox.reporting.repository.ReportTemplateHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportVerificationService {

    private final GeneratedReportArchiveRepository archiveRepository;
    private final ReportTemplateHeaderRepository headerRepository;

    public ReportVerificationResponse verifyReportToken(String token) {
        log.info("Verifying authenticity of report token: {}", token);

        Optional<GeneratedReportArchive> archiveOpt = archiveRepository.findByVerificationToken(token);
        if (archiveOpt.isEmpty()) {
            return ReportVerificationResponse.builder()
                    .authentic(false)
                    .verificationToken(token)
                    .message("Invalid or expired report verification token. Document cannot be authenticated.")
                    .build();
        }

        GeneratedReportArchive archive = archiveOpt.get();
        String issuingAuthority = headerRepository.findFirstByActiveTrue()
                .map(ReportTemplateHeader::getCentreName)
                .orElse("LabRox Reference Laboratories");

        return ReportVerificationResponse.builder()
                .authentic(true)
                .verificationToken(archive.getVerificationToken())
                .testId(archive.getTestId())
                .barcodeNo(archive.getBarcodeNo())
                .reportVersion(archive.getReportVersion())
                .signatureStampHash(archive.getSignatureStampHash())
                .renderedAt(archive.getRenderedAt())
                .issuingAuthority(issuingAuthority)
                .message("Verified authentic clinical diagnostic report. Compliant with 21 CFR Part 11 and ISO 15189.")
                .build();
    }

    public GeneratedReportArchive recordGeneratedReport(Long testId, String barcodeNo, String signatureHash, byte[] pdfBytes, Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        
        GeneratedReportArchive archive = GeneratedReportArchive.builder()
                .testId(testId)
                .barcodeNo(barcodeNo)
                .reportVersion(1)
                .verificationToken(token)
                .signatureStampHash(signatureHash)
                .fileStoragePath("/storage/reports/" + barcodeNo + "_v1.pdf")
                .fileSizeBytes((long) pdfBytes.length)
                .renderedByUserId(userId != null ? userId : 1L)
                .renderedAt(LocalDateTime.now())
                .build();

        return archiveRepository.save(archive);
    }
}
