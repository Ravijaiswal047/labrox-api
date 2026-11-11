package com.labrox.reporting.controller;

import com.labrox.reporting.domain.GeneratedReportArchive;
import com.labrox.reporting.domain.ReportTemplateHeader;
import com.labrox.reporting.dto.ReportDataDto;
import com.labrox.reporting.dto.ReportObservationDto;
import com.labrox.reporting.dto.ReportVerificationResponse;
import com.labrox.reporting.repository.ReportTemplateHeaderRepository;
import com.labrox.reporting.service.PdfReportGeneratorService;
import com.labrox.reporting.service.ReportVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Diagnostic Reporting Engine", description = "21 CFR Part 11 and ISO 15189 compliant document generation and verification APIs")
public class ReportingController {

    private final PdfReportGeneratorService pdfGeneratorService;
    private final ReportVerificationService verificationService;
    private final ReportTemplateHeaderRepository headerRepository;

    @GetMapping("/headers")
    @Operation(summary = "Get active center letterhead template")
    public ResponseEntity<ReportTemplateHeader> getActiveHeader() {
        return headerRepository.findFirstByActiveTrue()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(ReportTemplateHeader.builder()
                        .centreName("LabRox Reference Laboratories")
                        .nablAccNo("MC-3819")
                        .city("Bengaluru")
                        .build()));
    }

    @GetMapping("/preview/{testId}")
    @Operation(summary = "Get report JSON metadata and observations for preview")
    public ResponseEntity<ReportDataDto> getReportPreview(@PathVariable("testId") Long testId) {
        ReportDataDto data = buildSampleReportData(testId);
        return ResponseEntity.ok(data);
    }

    @GetMapping(value = "/pdf/{testId}", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Generate and stream pixel-perfect A4 diagnostic PDF report")
    public ResponseEntity<byte[]> streamPdfReport(@PathVariable("testId") Long testId) {
        log.info("Request to stream PDF report for testId: {}", testId);

        ReportDataDto data = buildSampleReportData(testId);
        byte[] pdfBytes = pdfGeneratorService.generateDiagnosticReportPdf(data);

        // Record generation in archive
        GeneratedReportArchive archive = verificationService.recordGeneratedReport(
                testId,
                data.getBarcodeNo(),
                data.getSignatureHash(),
                pdfBytes,
                1L
        );
        data.setVerificationToken(archive.getVerificationToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "Report_" + data.getBarcodeNo() + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/verify/{token}")
    @Operation(summary = "Verify report authenticity by cryptographic token")
    public ResponseEntity<ReportVerificationResponse> verifyReport(@PathVariable("token") String token) {
        ReportVerificationResponse response = verificationService.verifyReportToken(token);
        return ResponseEntity.ok(response);
    }

    private ReportDataDto buildSampleReportData(Long testId) {
        List<ReportObservationDto> obs = new ArrayList<>();
        obs.add(ReportObservationDto.builder()
                .analyteName("Serum Glucose (Fasting)")
                .resultValue("148.5")
                .unit("mg/dL")
                .biologicalInterval("70.0 - 99.0")
                .flag("HIGH")
                .abnormal(true)
                .build());

        obs.add(ReportObservationDto.builder()
                .analyteName("Hemoglobin A1c (HbA1c)")
                .resultValue("7.4")
                .unit("%")
                .biologicalInterval("4.0 - 5.6")
                .flag("HIGH")
                .abnormal(true)
                .build());

        obs.add(ReportObservationDto.builder()
                .analyteName("Estimated Average Glucose (eAG)")
                .resultValue("165.5")
                .unit("mg/dL")
                .biologicalInterval("90.0 - 120.0")
                .flag("HIGH")
                .abnormal(true)
                .build());

        obs.add(ReportObservationDto.builder()
                .analyteName("Total Cholesterol")
                .resultValue("182.0")
                .unit("mg/dL")
                .biologicalInterval("< 200.0")
                .flag("NORMAL")
                .abnormal(false)
                .build());

        obs.add(ReportObservationDto.builder()
                .analyteName("Triglycerides")
                .resultValue("142.0")
                .unit("mg/dL")
                .biologicalInterval("< 150.0")
                .flag("NORMAL")
                .abnormal(false)
                .build());

        obs.add(ReportObservationDto.builder()
                .analyteName("HDL Cholesterol")
                .resultValue("48.0")
                .unit("mg/dL")
                .biologicalInterval("> 40.0")
                .flag("NORMAL")
                .abnormal(false)
                .build());

        obs.add(ReportObservationDto.builder()
                .analyteName("Serum Creatinine")
                .resultValue("0.92")
                .unit("mg/dL")
                .biologicalInterval("0.70 - 1.20")
                .flag("NORMAL")
                .abnormal(false)
                .build());

        ReportTemplateHeader header = headerRepository.findFirstByActiveTrue().orElse(null);

        return ReportDataDto.builder()
                .testId(testId)
                .barcodeNo("S26308001001")
                .uhid("UHID-2026-90412")
                .patientName("Ramesh Kumar Gupta")
                .ageYears(48)
                .gender("MALE")
                .referredByDoctor("Dr. Aris Thorne, MD (Internal Medicine)")
                .specimenType("Venous Whole Blood / Fluoride & SST Serum")
                .collectionTime(LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")))
                .approvalTime(LocalDateTime.now().minusMinutes(20).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")))
                .testName("COMPREHENSIVE DIABETIC & METABOLIC PANEL")
                .department("CLINICAL BIOCHEMISTRY & ENDOCRINOLOGY")
                .centerName(header != null ? header.getCentreName() : "LABROX CENTRAL REFERENCE LABORATORIES")
                .nablAccNo(header != null ? header.getNablAccNo() : "MC-3819")
                .addressLine(header != null ? header.getAddressLine1() + ", " + header.getCity() : "104 Healthcare Boulevard, Bengaluru")
                .contactLine(header != null ? "Tel: " + header.getPhone() + " | Email: " + header.getEmail() : "Tel: +91 80 4000 5000")
                .pathologistName("Dr. Rajesh Sharma, MD (Pathology)")
                .medicalRegNo("KMC-58291")
                .signatureHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
                .verificationToken("VRF-9821-X91A")
                .observations(obs)
                .build();
    }
}
