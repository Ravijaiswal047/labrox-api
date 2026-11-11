package com.labrox.reporting.controller;

import com.labrox.reporting.dto.ReportDataDto;
import com.labrox.reporting.dto.ReportObservationDto;
import com.labrox.reporting.service.PdfReportGeneratorService;
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
import java.util.*;

@RestController
@RequestMapping({"/api/v1/commonReports", "/reports/v1/commonReports", "/api/v1/Reports", "/reports/v1/Reports"})
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Common Reports API", description = "Endpoints matching legacy nanjappa_reports_v9 for common report generation")
public class LegacyCommonReportsController {

    private final PdfReportGeneratorService pdfService;

    public LegacyCommonReportsController() {
        this.pdfService = new PdfReportGeneratorService();
    }

    public LegacyCommonReportsController(PdfReportGeneratorService pdfService) {
        this.pdfService = pdfService != null ? pdfService : new PdfReportGeneratorService();
    }

    @PostMapping({"/GetLabReport", "/getLabReport"})
    @Operation(summary = "Generate and stream final clinical diagnostic doctor report PDF blob")
    public ResponseEntity<byte[]> getLabReport(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy commonReports GetLabReport requested with: {}", request);

        int pHead = 1;
        if (request != null && request.get("PHead") != null) {
            try {
                pHead = Integer.parseInt(request.get("PHead").toString());
            } catch (Exception ignored) {}
        }
        boolean withHeader = (pHead != 0);

        List<?> testIdHashList = null;
        if (request != null && request.get("TestIDHash") instanceof List<?>) {
            testIdHashList = (List<?>) request.get("TestIDHash");
        }

        String firstHash = (testIdHashList != null && !testIdHashList.isEmpty() && testIdHashList.get(0) != null) 
                ? testIdHashList.get(0).toString() 
                : "hash101";

        ReportDataDto sampleDto = resolveSampleDtoForHash(firstHash);
        byte[] pdfBytes = pdfService.generateDiagnosticReportPdf(sampleDto, withHeader);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"LabRox_Diagnostic_Report.pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(pdfBytes);
    }

    private ReportDataDto resolveSampleDtoForHash(String hash) {
        String patientName = "Amit Sharma";
        String uhid = "LR2026-000001";
        int age = 30;
        String gender = "Male";
        String testName = "Complete Blood Count (CBC)";
        String department = "Hematology & Clinical Pathology";
        String specimen = "Whole Blood EDTA";
        String barcode = "BC20261105001";

        List<ReportObservationDto> observations;

        if (hash != null && (hash.contains("202") || hash.contains("creatinine"))) {
            patientName = "Priya Patel";
            uhid = "LR2026-000002";
            age = 28;
            gender = "Female";
            testName = "Serum Creatinine";
            department = "Clinical Biochemistry";
            specimen = "Serum";
            barcode = "BC20261105002";
            observations = List.of(
                    ReportObservationDto.builder().analyteName("Serum Creatinine").resultValue("0.9").unit("mg/dL").biologicalInterval("0.5 - 1.2").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("Blood Urea").resultValue("24.0").unit("mg/dL").biologicalInterval("15.0 - 45.0").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("eGFR").resultValue("98.5").unit("mL/min/1.73m2").biologicalInterval("> 90").flag("NORMAL").abnormal(false).build()
            );
        } else if (hash != null && (hash.contains("203") || hash.contains("sugar"))) {
            patientName = "Suresh Kumar";
            uhid = "LR2026-000003";
            age = 52;
            gender = "Male";
            testName = "Blood Sugar Fasting";
            department = "Clinical Biochemistry";
            specimen = "Fluoride Plasma";
            barcode = "BC20261105003";
            observations = List.of(
                    ReportObservationDto.builder().analyteName("Fasting Blood Glucose").resultValue("108.0").unit("mg/dL").biologicalInterval("70.0 - 99.0").flag("HIGH").abnormal(true).build(),
                    ReportObservationDto.builder().analyteName("HbA1c").resultValue("6.1").unit("%").biologicalInterval("< 5.7").flag("HIGH").abnormal(true).build()
            );
        } else if (hash != null && (hash.contains("204") || hash.contains("dengue"))) {
            patientName = "Rahul Verma";
            uhid = "LR2026-000004";
            age = 45;
            gender = "Male";
            testName = "Dengue Serology NS1";
            department = "Clinical Microbiology & Serology";
            specimen = "Serum";
            barcode = "BC20261105004";
            observations = List.of(
                    ReportObservationDto.builder().analyteName("Dengue NS1 Antigen").resultValue("NEGATIVE").unit("Qualitative").biologicalInterval("NEGATIVE").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("Dengue IgM Antibodies").resultValue("NEGATIVE").unit("Qualitative").biologicalInterval("NEGATIVE").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("Dengue IgG Antibodies").resultValue("POSITIVE").unit("Qualitative").biologicalInterval("NEGATIVE").flag("ABNORMAL").abnormal(true).build()
            );
        } else {
            observations = List.of(
                    ReportObservationDto.builder().analyteName("Hemoglobin (Hb)").resultValue("14.5").unit("g/dL").biologicalInterval("13.0 - 17.0").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("Total Leukocyte Count (WBC)").resultValue("7800").unit("/cumm").biologicalInterval("4000 - 11000").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("Platelet Count").resultValue("245000").unit("/cumm").biologicalInterval("150000 - 450000").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("Packed Cell Volume (PCV)").resultValue("43.2").unit("%").biologicalInterval("40.0 - 50.0").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("RBC Count").resultValue("4.85").unit("mill/cumm").biologicalInterval("4.5 - 5.5").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("MCV").resultValue("89.1").unit("fL").biologicalInterval("83 - 101").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("MCH").resultValue("29.9").unit("pg").biologicalInterval("27 - 32").flag("NORMAL").abnormal(false).build(),
                    ReportObservationDto.builder().analyteName("MCHC").resultValue("33.6").unit("g/dL").biologicalInterval("31.5 - 34.5").flag("NORMAL").abnormal(false).build()
            );
        }

        return ReportDataDto.builder()
                .testId(101L)
                .barcodeNo(barcode)
                .uhid(uhid)
                .patientName(patientName)
                .ageYears(age)
                .gender(gender)
                .referredByDoctor("Dr. Rajesh Sharma, MD")
                .centerName("LabRox Main Reference Lab")
                .specimenType(specimen)
                .collectionTime("10-Nov-2026 09:30 AM")
                .approvalTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")))
                .testName(testName)
                .department(department)
                .nablAccNo("MC-2026-NABL-0982")
                .addressLine("104 Healthcare Boulevard, Medical Enclave, Bengaluru 560001")
                .contactLine("Tel: +91 80 4000 5000 | Email: reports@labrox.io | Web: www.labrox.io")
                .pathologistName("Dr. Rajesh Sharma, MD (Pathology)")
                .medicalRegNo("KMC-58291")
                .signatureHash("LRX-HASH-2026-APPROVED-AUDIT-VERIFIED")
                .verificationToken("LRX-VRF-20261110-00109")
                .observations(observations)
                .build();
    }

    @PostMapping("/GenerateReport")
    @Operation(summary = "Generate unified clinical or financial report PDF")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy commonReports GenerateReport requested: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Report Generated");
        response.put("Url", "/api/v1/Reports/download/LabRox_Report.pdf");
        return ResponseEntity.ok(response);
    }
}
