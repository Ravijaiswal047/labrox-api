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
@RequestMapping({"/api/v1/SRA", "/api/v1/sra"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy SRA Sample Accessioning and Sorting API")
public class LegacySRAController {

    @PostMapping({"/SearchPendingData", "/searchPendingData"})
    @Operation(summary = "Search pending samples for SRA routing and sorting")
    public ResponseEntity<Map<String, Object>> searchPendingData(@RequestBody(required = false) Map<String, Object> request) {
        log.info("SRA SearchPendingData called with: {}", request);
        List<Map<String, Object>> rows = ClinicalSampleStore.getSraPendingData(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", rows);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SampleSearch", "/sampleSearch"})
    @Operation(summary = "Perform barcode scan sorting into In-House or Segregation")
    public ResponseEntity<Map<String, Object>> sampleSearch(@RequestBody(required = false) Object request) {
        log.info("SRA SampleSearch called with: {}", request);

        String barcode = "";
        if (request instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?, ?> map && map.get("barcodeNo") != null) {
                barcode = map.get("barcodeNo").toString();
            }
        } else if (request instanceof Map<?, ?> map && map.get("barcodeNo") != null) {
            barcode = map.get("barcodeNo").toString();
        }

        Map<String, Object> response = ClinicalSampleStore.sraSampleSearch(barcode);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SampleReject", "/sampleReject"})
    @Operation(summary = "Reject sample at SRA sorting stage")
    public ResponseEntity<Map<String, Object>> sampleReject(@RequestBody(required = false) Map<String, Object> request) {
        log.info("SRA sampleReject called with: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample Rejected at SRA");
        return ResponseEntity.ok(response);
    }
}
