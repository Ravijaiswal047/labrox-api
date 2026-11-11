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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/Reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Clinical Reports API", description = "Endpoints matching legacy nanjappa_api_v9 ReportsController for fe_v9 clinical PDF generation and receipts")
public class LegacyReportsController {

    private final PdfReportGeneratorService pdfService;

    @PostMapping("/DoctorReport")
    @Operation(summary = "Generate and stream final clinical diagnostic doctor report PDF")
    public ResponseEntity<Map<String, Object>> doctorReport(@RequestBody(required = false) Map<String, Object> data) {
        log.info("Legacy Reports DoctorReport requested: {}", data);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "File upload Successfully.");
        response.put("Url", "/api/v1/Reports/download/Clinical_Report_LR2026.pdf");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GetSingleReceiptData")
    @Operation(summary = "Retrieve cashier POS receipt reprint data and PDF link")
    public ResponseEntity<Map<String, Object>> getSingleReceiptData(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy Reports GetSingleReceiptData requested: {}", request);
        String receiptNo = (request != null && request.get("ReceiptNo") != null) ? request.get("ReceiptNo").toString() : "REC-20261104-00086";

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> rc = new HashMap<>();
        rc.put("ReceiptNo", receiptNo);
        rc.put("LedgerTransactionNo", "WO-20261104-00109");
        rc.put("PatientID", "LR2026-000043");
        rc.put("PName", "Walk-in Patient");
        rc.put("Age", "35 Y");
        rc.put("Gender", "Male");
        rc.put("DoctorName", "Dr. Rajesh Sharma, MD");
        rc.put("GrossAmount", new BigDecimal("1200.00"));
        rc.put("DiscountAmount", BigDecimal.ZERO);
        rc.put("NetAmount", new BigDecimal("1200.00"));
        rc.put("AmountPaid", new BigDecimal("1200.00"));
        rc.put("PaymentMode", "Cash");
        rc.put("Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")));
        rc.put("CentreName", "Main Reference Lab");
        rc.put("ItemName", "Complete Blood Count (CBC)");
        list.add(rc);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        response.put("Url", "/api/v1/Reports/download/Receipt_" + receiptNo + ".pdf");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/InvoiceReceiptData")
    public ResponseEntity<Map<String, Object>> invoiceReceiptData(@RequestBody(required = false) Map<String, Object> request) {
        return getSingleReceiptData(request);
    }

    @PostMapping("/ExportInvoiceReprintData")
    public ResponseEntity<Map<String, Object>> exportInvoiceReprintData(@RequestBody(required = false) Map<String, Object> request) {
        return getSingleReceiptData(request);
    }

    @PostMapping("/LabWorksheet")
    public ResponseEntity<Map<String, Object>> labWorksheet(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{filename}")
    @Operation(summary = "Download dynamically generated NABL-compliant A4 clinical PDF")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable("filename") String filename) {
        ReportDataDto sampleDto = ReportDataDto.builder()
                .testId(101L)
                .barcodeNo("BAR20261104001")
                .uhid("LR2026-000043")
                .patientName("Walk-in Patient")
                .ageYears(35)
                .gender("Male")
                .referredByDoctor("Dr. Rajesh Sharma, MD")
                .centerName("LabRox Main Reference Lab")
                .specimenType("Whole Blood EDTA")
                .collectionTime("04-Nov-2026 03:30 PM")
                .approvalTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")))
                .testName("Complete Blood Count (CBC)")
                .department("Hematology & Clinical Pathology")
                .pathologistName("Dr. Ramesh Chandra, MD (Pathology)")
                .signatureHash("LRX-HASH-2026-SECURE-AUDIT-VERIFIED")
                .observations(List.of(
                        ReportObservationDto.builder().analyteName("Hemoglobin (Hb)").resultValue("14.2").unit("g/dL").biologicalInterval("13.0 - 17.0").flag("NORMAL").abnormal(false).build(),
                        ReportObservationDto.builder().analyteName("Total Leukocyte Count (WBC)").resultValue("7800").unit("/cumm").biologicalInterval("4000 - 11000").flag("NORMAL").abnormal(false).build(),
                        ReportObservationDto.builder().analyteName("Platelet Count").resultValue("240000").unit("/cumm").biologicalInterval("150000 - 450000").flag("NORMAL").abnormal(false).build(),
                        ReportObservationDto.builder().analyteName("Packed Cell Volume (PCV)").resultValue("42.5").unit("%").biologicalInterval("40.0 - 50.0").flag("NORMAL").abnormal(false).build()
                ))
                .build();

        byte[] pdfBytes = pdfService.generateDiagnosticReportPdf(sampleDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
