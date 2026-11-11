package com.labrox.reporting.service;

import com.labrox.reporting.dto.ReportDataDto;
import com.labrox.reporting.dto.ReportObservationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportGeneratorServiceTest {

    private PdfReportGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new PdfReportGeneratorService();
    }

    @Test
    @DisplayName("Generate Diagnostic PDF produces valid A4 PDF bytes with %PDF magic header")
    void testGenerateDiagnosticReportPdf() {
        ReportObservationDto obs1 = ReportObservationDto.builder()
                .analyteName("Hemoglobin A1c")
                .resultValue("7.4")
                .unit("%")
                .biologicalInterval("4.0 - 5.6")
                .flag("HIGH")
                .abnormal(true)
                .build();

        ReportObservationDto obs2 = ReportObservationDto.builder()
                .analyteName("Serum Creatinine")
                .resultValue("0.90")
                .unit("mg/dL")
                .biologicalInterval("0.70 - 1.20")
                .flag("NORMAL")
                .abnormal(false)
                .build();

        ReportDataDto data = ReportDataDto.builder()
                .testId(101L)
                .barcodeNo("S26308001001")
                .uhid("UHID-1002")
                .patientName("Anil Deshmukh")
                .ageYears(52)
                .gender("MALE")
                .referredByDoctor("Dr. Sarah Vance")
                .specimenType("Whole Blood EDTA")
                .collectionTime("03-Nov-2026 10:15")
                .approvalTime("03-Nov-2026 12:45")
                .testName("DIABETIC PANEL")
                .department("BIOCHEMISTRY")
                .centerName("LabRox Reference Laboratory")
                .nablAccNo("MC-3819")
                .pathologistName("Dr. Rajesh Sharma, MD")
                .medicalRegNo("KMC-58291")
                .signatureHash("a1b2c3d4e5f67890abcdef1234567890abcdef1234567890abcdef1234567890")
                .verificationToken("VRF-TEST-1234")
                .observations(List.of(obs1, obs2))
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000, "Generated PDF should be at least 1KB in size");

        // Verify PDF Magic Bytes (%PDF)
        String header = new String(pdfBytes, 0, 4, StandardCharsets.US_ASCII);
        assertEquals("%PDF", header, "Generated file must start with '%PDF' magic header");
    }
}
