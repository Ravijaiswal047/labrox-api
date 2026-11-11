package com.labrox.reporting.service;

import com.labrox.reporting.dto.ReportDataDto;
import com.labrox.reporting.dto.ReportObservationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportGeneratorServiceTest {

    private PdfReportGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new PdfReportGeneratorService();
        new File("target").mkdirs();
    }

    @Test
    @DisplayName("Test Case 1: Exact Reference Match - CREATININE-SERUM (Biochemistry, Mrs. Anjuma Bibi)")
    void testGenerateCompleteBloodCountPdf() throws Exception {
        List<ReportObservationDto> obs = List.of(
                ReportObservationDto.builder()
                        .analyteName("CREATININE-SERUM")
                        .resultValue("0.61")
                        .unit("mg/dL")
                        .biologicalInterval("0.40-1.10")
                        .flag("NORMAL")
                        .abnormal(false)
                        .build()
        );

        ReportDataDto data = ReportDataDto.builder()
                .testId(202L)
                .barcodeNo("SIL2610030424")
                .uhid("LRPL/02206585")
                .patientName("Mrs. ANJUMA BIBI")
                .ageYears(55)
                .gender("Female")
                .referredByDoctor("Dr. R KR DARIPA")
                .specimenType("Serum")
                .collectionTime("03/Oct/2026 08:56 PM")
                .approvalTime("03/Oct/2026 10:25 PM")
                .testName("CREATININE-SERUM")
                .department("BIOCHEMISTRY")
                .centerName("Labrox Diagnostics Pvt. Ltd.")
                .nablAccNo("MC-3819")
                .pathologistName("DR. S. BHATTACHARJEE")
                .medicalRegNo("WBMC69081")
                .signatureHash("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                .verificationToken("SIL2610030424")
                .observations(obs)
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 3000);
        assertEquals("%PDF", new String(pdfBytes, 0, 4, StandardCharsets.US_ASCII));

        try (FileOutputStream fos = new FileOutputStream("target/test_case_1_cbc.pdf")) {
            fos.write(pdfBytes);
        }
    }

    @Test
    @DisplayName("Test Case 2: Exact Reference Match - THYROID STIMULATING HORMONE (TSH) (Immunology)")
    void testGenerateLipidProfilePdf() throws Exception {
        List<ReportObservationDto> obs = List.of(
                ReportObservationDto.builder()
                        .analyteName("THYROID STIMULATING HORMONE (TSH),Serum")
                        .resultValue("2.767")
                        .unit("µIU/ml")
                        .biologicalInterval("0.35-5.50")
                        .flag("NORMAL")
                        .abnormal(false)
                        .build()
        );

        ReportDataDto data = ReportDataDto.builder()
                .testId(205L)
                .barcodeNo("SIL2610030424")
                .uhid("LRPL/02206585")
                .patientName("Mrs. ANJUMA BIBI")
                .ageYears(55)
                .gender("Female")
                .referredByDoctor("Dr. R KR DARIPA")
                .specimenType("Serum")
                .collectionTime("03/Oct/2026 08:56 PM")
                .approvalTime("03/Oct/2026 10:35 PM")
                .testName("THYROID STIMULATING HORMONE (TSH)")
                .department("IMMUNOLOGY")
                .centerName("Labrox Diagnostics Pvt. Ltd.")
                .nablAccNo("MC-3819")
                .pathologistName("DR. S. BHATTACHARJEE")
                .medicalRegNo("WBMC69081")
                .signatureHash("9f83c60579e52ab7eb5b25b2e0bd9b9f0ac0731fc597ddd8e00c4e51e082942b")
                .verificationToken("SIL2610030424")
                .observations(obs)
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 3000);

        try (FileOutputStream fos = new FileOutputStream("target/test_case_2_lipid.pdf")) {
            fos.write(pdfBytes);
        }
    }

    @Test
    @DisplayName("Test Case 3: Complete Blood Count & Comprehensive Multi-page Report")
    void testGenerateMultiPageOverflowPdf() throws Exception {
        List<ReportObservationDto> obs = List.of(
                ReportObservationDto.builder().analyteName("Hemoglobin (Hb)").resultValue("11.2").unit("g/dL").biologicalInterval("13.0 - 17.0").flag("LOW").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Total Leukocyte Count (WBC)").resultValue("14500").unit("/cumm").biologicalInterval("4000 - 11000").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Platelet Count").resultValue("210000").unit("/cumm").biologicalInterval("150000 - 450000").flag("NORMAL").abnormal(false).build(),
                ReportObservationDto.builder().analyteName("Packed Cell Volume (PCV)").resultValue("34.5").unit("%").biologicalInterval("40.0 - 50.0").flag("LOW").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("RBC Count").resultValue("3.85").unit("mill/cumm").biologicalInterval("4.5 - 5.5").flag("LOW").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Mean Corpuscular Volume (MCV)").resultValue("89.6").unit("fL").biologicalInterval("83.0 - 101.0").flag("NORMAL").abnormal(false).build(),
                ReportObservationDto.builder().analyteName("Mean Corpuscular Hemoglobin (MCH)").resultValue("29.1").unit("pg").biologicalInterval("27.0 - 32.0").flag("NORMAL").abnormal(false).build(),
                ReportObservationDto.builder().analyteName("MCHC").resultValue("32.5").unit("g/dL").biologicalInterval("31.5 - 34.5").flag("NORMAL").abnormal(false).build(),
                ReportObservationDto.builder().analyteName("Red Cell Distribution Width (RDW-CV)").resultValue("15.8").unit("%").biologicalInterval("11.5 - 14.5").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Neutrophils").resultValue("78").unit("%").biologicalInterval("40 - 75").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Lymphocytes").resultValue("18").unit("%").biologicalInterval("20 - 45").flag("LOW").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Eosinophils").resultValue("2").unit("%").biologicalInterval("1 - 6").flag("NORMAL").abnormal(false).build(),
                ReportObservationDto.builder().analyteName("Monocytes").resultValue("2").unit("%").biologicalInterval("2 - 10").flag("NORMAL").abnormal(false).build()
        );

        ReportDataDto data = ReportDataDto.builder()
                .testId(301L)
                .barcodeNo("SIL2610030424")
                .uhid("LRPL/02206585")
                .patientName("Mrs. ANJUMA BIBI")
                .ageYears(55)
                .gender("Female")
                .referredByDoctor("Dr. R KR DARIPA")
                .specimenType("Serum - SL260287")
                .collectionTime("03/Oct/2026 08:56 PM")
                .approvalTime("03/Oct/2026 10:25 PM")
                .testName("COMPLETE BLOOD COUNT (CBC)")
                .department("HEMATOLOGY")
                .centerName("Labrox Diagnostics Pvt. Ltd.")
                .nablAccNo("MC-3819")
                .pathologistName("DR. S. BHATTACHARJEE")
                .medicalRegNo("WBMC69081")
                .signatureHash("b72da93c8340d21a9f7f45b597ddd8e00c4e51e082942bc1")
                .verificationToken("SIL2610030424")
                .observations(obs)
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 4000);

        try (FileOutputStream fos = new FileOutputStream("target/test_case_3_multipage.pdf")) {
            fos.write(pdfBytes);
        }
    }
}
