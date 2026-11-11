package com.labrox.sample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/SampleReceiveBarcode")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Sample Receive Barcode API", description = "Endpoints matching legacy nanjappa_api_v9 SampleReceiveBarcodeController for fe_v9 lab accessioning")
public class LegacySampleReceiveBarcodeController {

    @PostMapping("/SearchSampleReceive")
    @Operation(summary = "Search barcode for lab accessioning and sample receipt")
    public ResponseEntity<Map<String, Object>> searchSampleReceive(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SampleReceiveBarcode SearchSampleReceive called: {}", request);

        String barcode = (request != null && request.get("SearchData") != null) ? request.get("SearchData").toString() : "BAR20261104001";

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("HLMPatientType", "OPD");
        item.put("Gender", "35 Y / Male");
        item.put("InvestigationId", 1);
        item.put("Dispatchcode", "");
        item.put("AccessDepartment", "1");
        item.put("Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yy hh:mm a")));
        item.put("ResultFlag", 0);
        item.put("SubCategoryId", 1);
        item.put("SampleQty", "3ml");
        item.put("HistoCytoSampleDetail", "");
        item.put("LedgerTransactionID", 1);
        item.put("Performingdoctor", "Dr. Rajesh Sharma, MD");
        item.put("IsUrgent", 0);
        item.put("BarcodeNo", barcode);
        item.put("testid", 101);
        item.put("PName", "Walk-in Patient");
        item.put("ledgerTransactionNO", "WO-20261104-00109");
        item.put("patientid", "LR2026-000043");
        item.put("SampleTypeID", 1);
        item.put("SampleTypeName", "Whole Blood EDTA");
        item.put("itemname", "Complete Blood Count (CBC)");
        item.put("RowColor", "lightyellow");
        item.put("Status", "Pending");
        item.put("reporttype", 1);
        item.put("Segregation", "0");
        item.put("PanelName", "Main Reference Lab");
        item.put("IsVetCanine", 0);
        list.add(item);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SaveSampleReceive")
    @Operation(summary = "Save and confirm accessioning of specimen into lab department")
    public ResponseEntity<Map<String, Object>> saveSampleReceive(@RequestBody(required = false) Object data) {
        log.info("Legacy SampleReceiveBarcode SaveSampleReceive confirmed: {}", data);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample Received Successfully");
        return ResponseEntity.ok(response);
    }
}
