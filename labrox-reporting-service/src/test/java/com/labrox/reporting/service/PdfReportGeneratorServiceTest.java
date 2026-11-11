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
    @DisplayName("Test Case 1: Standard CBC with 10+ parameters, abnormal flags, and edge cases")
    void testGenerateCompleteBloodCountPdf() throws Exception {
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
                .testId(201L)
                .barcodeNo("BC20261111001")
                .uhid("UHID-LR2026-9081")
                .patientName("Ramesh Chandran")
                .ageYears(42)
                .gender("MALE")
                .referredByDoctor("Dr. S. K. Bhardwaj, MD")
                .specimenType("Whole Blood EDTA")
                .collectionTime("11-Nov-2026 08:30 AM")
                .approvalTime("11-Nov-2026 10:15 AM")
                .testName("COMPLETE BLOOD COUNT (CBC)")
                .department("HEMATOLOGY & CLINICAL PATHOLOGY")
                .centerName("LabRox Reference Laboratories")
                .nablAccNo("MC-3819")
                .pathologistName("Dr. Rajesh Sharma, MD (Pathology)")
                .medicalRegNo("KMC-58291")
                .signatureHash("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                .verificationToken("LRX-VRF-20261111-00201")
                .observations(obs)
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 5000);
        assertEquals("%PDF", new String(pdfBytes, 0, 4, StandardCharsets.US_ASCII));

        try (FileOutputStream fos = new FileOutputStream("target/test_case_1_cbc.pdf")) {
            fos.write(pdfBytes);
        }
    }

    @Test
    @DisplayName("Test Case 2: Comprehensive Lipid Profile (calculated values and risk intervals)")
    void testGenerateLipidProfilePdf() throws Exception {
        List<ReportObservationDto> obs = List.of(
                ReportObservationDto.builder().analyteName("Total Cholesterol").resultValue("235.0").unit("mg/dL").biologicalInterval("< 200.0").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Triglycerides").resultValue("185.0").unit("mg/dL").biologicalInterval("< 150.0").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("HDL Cholesterol (Direct)").resultValue("38.0").unit("mg/dL").biologicalInterval("40.0 - 60.0").flag("LOW").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("LDL Cholesterol (Calculated)").resultValue("160.0").unit("mg/dL").biologicalInterval("< 100.0").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("VLDL Cholesterol").resultValue("37.0").unit("mg/dL").biologicalInterval("10.0 - 30.0").flag("HIGH").abnormal(true).build(),
                ReportObservationDto.builder().analyteName("Cholesterol / HDL Ratio").resultValue("6.18").unit("Ratio").biologicalInterval("3.30 - 4.40").flag("HIGH").abnormal(true).build()
        );

        ReportDataDto data = ReportDataDto.builder()
                .testId(205L)
                .barcodeNo("BC20261111002")
                .uhid("UHID-LR2026-9082")
                .patientName("Meera Krishnan")
                .ageYears(48)
                .gender("FEMALE")
                .referredByDoctor("Dr. Anita Sen, MD (Cardiology)")
                .specimenType("Serum - Fasting (12 hrs)")
                .collectionTime("11-Nov-2026 07:45 AM")
                .approvalTime("11-Nov-2026 09:30 AM")
                .testName("LIPID PROFILE EXTENDED")
                .department("CLINICAL BIOCHEMISTRY")
                .centerName("LabRox Reference Laboratories")
                .nablAccNo("MC-3819")
                .pathologistName("Dr. Rajesh Sharma, MD (Pathology)")
                .medicalRegNo("KMC-58291")
                .signatureHash("9f83c60579e52ab7eb5b25b2e0bd9b9f0ac0731fc597ddd8e00c4e51e082942b")
                .verificationToken("LRX-VRF-20261111-00205")
                .observations(obs)
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 4000);

        try (FileOutputStream fos = new FileOutputStream("target/test_case_2_lipid.pdf")) {
            fos.write(pdfBytes);
        }
    }

    @Test
    @DisplayName("Test Case 3: Multi-page overflow report demonstrating repeated headers and Page X of Y")
    void testGenerateMultiPageOverflowPdf() throws Exception {
        List<ReportObservationDto> obs = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            boolean abnormal = (i % 3 == 0);
            obs.add(ReportObservationDto.builder()
                    .analyteName("Analyte Investigation Parameter #" + i + " (Clinical Marker)")
                    .resultValue(abnormal ? String.valueOf(100 + i * 15) : String.valueOf(20 + i))
                    .unit("mg/dL")
                    .biologicalInterval("10.0 - 50.0")
                    .flag(abnormal ? "HIGH" : "NORMAL")
                    .abnormal(abnormal)
                    .build());
        }

        ReportDataDto data = ReportDataDto.builder()
                .testId(301L)
                .barcodeNo("BC20261111003")
                .uhid("UHID-LR2026-9083")
                .patientName("Vikramaditya Singhania")
                .ageYears(60)
                .gender("MALE")
                .referredByDoctor("Dr. H. V. Nanjappa, MS, FRCS")
                .specimenType("Serum & Plasma")
                .collectionTime("11-Nov-2026 06:30 AM")
                .approvalTime("11-Nov-2026 11:00 AM")
                .testName("COMPREHENSIVE METABOLIC MASTER PANEL (24 ANALYTES)")
                .department("SPECIALIZED BIOCHEMISTRY & ENDOCRINOLOGY")
                .centerName("LabRox Reference Laboratories")
                .nablAccNo("MC-3819")
                .pathologistName("Dr. Rajesh Sharma, MD (Pathology)")
                .medicalRegNo("KMC-58291")
                .signatureHash("cb5628607a300d53e3b10418427980d320cc1049d0f3f6e120d875fc4f20ad7d")
                .verificationToken("LRX-VRF-20261111-00301")
                .observations(obs)
                .build();

        byte[] pdfBytes = generatorService.generateDiagnosticReportPdf(data);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 6000);

        try (FileOutputStream fos = new FileOutputStream("target/test_case_3_multipage.pdf")) {
            fos.write(pdfBytes);
        }
    }
}
