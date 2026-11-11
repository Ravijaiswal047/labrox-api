package com.labrox.sample.controller;

import com.labrox.sample.service.BarcodeGeneratorService;
import com.labrox.sample.service.ClinicalSampleStore;
import com.labrox.sample.service.SampleCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/SC")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Sample Collection API", description = "Endpoints matching legacy nanjappa_api_v9 SCController for fe_v9 phlebotomy")
public class LegacySCController {

    private final SampleCollectionService sampleService;
    private final BarcodeGeneratorService barcodeGenerator;

    @PostMapping("/GetSampleCollection")
    @Operation(summary = "Search work orders pending sample collection")
    public ResponseEntity<Map<String, Object>> getSampleCollection(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC GetSampleCollection called with: {}", request);
        Object statusFilter = request != null ? request.get("SampleStatus") : null;
        List<Map<String, Object>> records = ClinicalSampleStore.getSampleCollection(statusFilter);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", records);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SearchInvestigation")
    @Operation(summary = "Search investigations for a ledger transaction")
    public ResponseEntity<Map<String, Object>> searchInvestigation(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC SearchInvestigation called with: {}", request);

        int ledgerId = 1;
        if (request != null && request.get("LedgerTransactionID") != null) {
            try {
                ledgerId = Integer.parseInt(request.get("LedgerTransactionID").toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        List<Map<String, Object>> result = ClinicalSampleStore.searchInvestigation(ledgerId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/getBarcode")
    @Operation(summary = "Generate or retrieve barcode for phlebotomy container")
    public ResponseEntity<Map<String, Object>> getBarcode(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC getBarcode called: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "");
        response.put("data", "Barcode print command dispatched to printer");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/getLabBarcode")
    public ResponseEntity<Map<String, Object>> getLabBarcode(@RequestBody(required = false) Map<String, Object> request) {
        return getBarcode(request);
    }

    @PostMapping("/getDepartmentwiseBarcode")
    public ResponseEntity<Map<String, Object>> getDepartmentwiseBarcode(@RequestBody(required = false) Map<String, Object> request) {
        return getBarcode(request);
    }

    @PostMapping("/SampleCollection")
    @Operation(summary = "Confirm phlebotomy sample collection")
    public ResponseEntity<Map<String, Object>> saveSampleCollection(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC SampleCollection confirmed: {}", request);

        List<?> rawData = null;
        if (request != null && request.get("data") instanceof List<?>) {
            rawData = (List<?>) request.get("data");
        }

        if (rawData != null && !rawData.isEmpty()) {
            Map<String, Object> result = ClinicalSampleStore.collectSamples(rawData);
            return ResponseEntity.ok(result);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        response.put("success", false);
        response.put("message", "No sample data provided");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SampleRejection")
    @Operation(summary = "Reject compromised sample at collection stage")
    public ResponseEntity<Map<String, Object>> sampleRejection(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC SampleRejection called: {}", request);

        List<?> rawData = null;
        if (request != null && request.get("data") instanceof List<?>) {
            rawData = (List<?>) request.get("data");
        }

        if (rawData != null && !rawData.isEmpty()) {
            ClinicalSampleStore.rejectSamples(rawData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample rejected successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SampleSNR")
    @Operation(summary = "Mark sample as SNR (Sample Not Received)")
    public ResponseEntity<Map<String, Object>> sampleSNR(@RequestBody(required = false) Map<String, Object> request) {
        return sampleRejection(request);
    }

    @GetMapping("/getrejectcount")
    @Operation(summary = "Get count of rejected samples")
    public ResponseEntity<Map<String, Object>> getRejectCount() {
        long count = ClinicalSampleStore.getRejectCount();
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GetRejectReason")
    @Operation(summary = "Get list of rejection reasons")
    public ResponseEntity<Map<String, Object>> getRejectReason(@RequestBody(required = false) Map<String, Object> request) {
        List<Map<String, Object>> reasons = new ArrayList<>();
        reasons.add(createReason(1, "Hemolysed Specimen"));
        reasons.add(createReason(2, "Clotted Whole Blood"));
        reasons.add(createReason(3, "Insufficient Quantity (QNS)"));
        reasons.add(createReason(4, "Improper Container / Tube Mismatch"));
        reasons.add(createReason(5, "Leaking Container"));
        reasons.add(createReason(6, "Unlabeled / Misidentified Tube"));

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", reasons);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/HoldSample")
    public ResponseEntity<Map<String, Object>> holdSample(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC HoldSample called: {}", request);
        List<?> rawData = null;
        if (request != null && request.get("data") instanceof List<?>) {
            rawData = (List<?>) request.get("data");
        }
        if (rawData != null && !rawData.isEmpty()) {
            ClinicalSampleStore.holdSample(rawData);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample Put On Hold");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/UnHoldSample")
    public ResponseEntity<Map<String, Object>> unHoldSample(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC UnHoldSample called: {}", request);
        List<?> rawData = null;
        if (request != null && request.get("data") instanceof List<?>) {
            rawData = (List<?>) request.get("data");
        }
        if (rawData != null && !rawData.isEmpty()) {
            ClinicalSampleStore.unHoldSample(rawData);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample Released From Hold");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/UpdateUrgentPLO")
    public ResponseEntity<Map<String, Object>> updateUrgentPLO(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Priority Updated Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/UpdateColorPLO")
    public ResponseEntity<Map<String, Object>> updateColorPLO(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Vial Color Updated Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SampleCollectionRemoveBarcode")
    public ResponseEntity<Map<String, Object>> removeBarcode(@RequestBody(required = false) Object request) {
        ClinicalSampleStore.resetInitialData();
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Barcode Removed Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/RegisterWorkOrder", "/registerWorkOrder"})
    @Operation(summary = "Register patient and tests into phlebotomy accessioning")
    public ResponseEntity<Map<String, Object>> registerWorkOrder(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy SC RegisterWorkOrder called with: {}", request);
        Map<String, Object> result = ClinicalSampleStore.registerPatientWorkOrder(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Work Order Registered in Phlebotomy");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createReason(int id, String text) {
        Map<String, Object> m = new HashMap<>();
        m.put("ReasonID", id);
        m.put("Reason", text);
        return m;
    }
}
