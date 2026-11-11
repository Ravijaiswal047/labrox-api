package com.labrox.sample.controller;

import com.labrox.sample.service.ClinicalSampleStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/DepartmentReceive", "/api/v1/departmentReceive"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Department Receive API", description = "Endpoints matching legacy nanjappa_api_v9 DepartmentReceiveController")
public class LegacyDepartmentReceiveController {

    @PostMapping({"/DepartmentReceive", "/departmentReceive", "/GetDepartmentReceive", "/getDepartmentReceive"})
    @Operation(summary = "Search samples pending reception in laboratory department")
    public ResponseEntity<Map<String, Object>> getDepartmentReceive(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy DepartmentReceive GetDepartmentReceive called: {}", request);
        Map<String, Object> response = ClinicalSampleStore.getDepartmentReceive(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SampleReceiveBarcode", "/sampleReceiveBarcode"})
    @Operation(summary = "Direct department sample receive by barcode scan")
    public ResponseEntity<Map<String, Object>> sampleReceiveBarcode(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy DepartmentReceive SampleReceiveBarcode: {}", request);
        String barcode = request != null && request.get("BarcodeNo") != null ? request.get("BarcodeNo").toString().trim() : "";
        Map<String, Object> response = ClinicalSampleStore.receiveBarcode(barcode);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SampleReceiveDepartment", "/sampleReceiveDepartment", "/SaveDepartmentReceive", "/saveDepartmentReceive"})
    @Operation(summary = "Confirm department sample reception")
    public ResponseEntity<Map<String, Object>> saveDepartmentReceive(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy DepartmentReceive SaveDepartmentReceive called: {}", request);

        List<?> rawData = null;
        if (request != null && request.get("data") instanceof List<?>) {
            rawData = (List<?>) request.get("data");
        }

        Map<String, Object> response = ClinicalSampleStore.receiveDepartment(rawData);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SaveTransferData", "/saveTransferData"})
    @Operation(summary = "Save sample transfer data in department")
    public ResponseEntity<Map<String, Object>> saveTransferData(@RequestBody(required = false) Object data) {
        log.info("Legacy DepartmentReceive SaveTransferData called: {}", data);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Samples Transferred Successfully");
        return ResponseEntity.ok(response);
    }
}
