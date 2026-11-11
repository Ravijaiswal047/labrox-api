package com.labrox.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Worksheet and Barcode Queue API", description = "Endpoints matching legacy nanjappa_api_v9 WorkSheetOrderController and BarcodeQueueController")
public class LegacyWorkSheetAndQueueController {

    @PostMapping("/WorkSheetOrder/GetWorkSheetOrder")
    @Operation(summary = "Search work order sheets for laboratory bench testing")
    public ResponseEntity<Map<String, Object>> getWorkSheetOrder(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy WorkSheetOrder GetWorkSheetOrder: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/BarcodeQueue/GetBarcodeQueueData")
    @Operation(summary = "Get queued barcodes for batch barcode thermal printing")
    public ResponseEntity<Map<String, Object>> getBarcodeQueueData(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy BarcodeQueue GetBarcodeQueueData: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }
}
