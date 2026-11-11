package com.labrox.diagnostic.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.dto.DeltaCheckResult;
import com.labrox.diagnostic.dto.SaveResultEntryRequest;
import com.labrox.diagnostic.service.DeltaCheckService;
import com.labrox.diagnostic.service.ResultEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/diagnostic")
@RequiredArgsConstructor
@Tag(name = "Clinical Diagnostic & Result Entry API", description = "Endpoints for result entry, historical delta checks, and panic alerts")
public class DiagnosticController {

    private final ResultEntryService resultEntryService;
    private final DeltaCheckService deltaCheckService;

    @PostMapping("/results/save")
    @Operation(summary = "Save entered results, evaluate panic limits, and trigger delta checks")
    public ResponseEntity<ApiResponse<PatientLabInvestigation>> saveResults(@RequestBody SaveResultEntryRequest request) {
        return resultEntryService.saveResults(request)
                .map(inv -> ResponseEntity.ok(ApiResponse.ok("Test results saved successfully", inv)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/worklist")
    @Operation(summary = "Retrieve diagnostic bench worklist by status (e.g. SAMPLE_RECEIVED, RESULT_ENTERED)")
    public ResponseEntity<ApiResponse<List<PatientLabInvestigation>>> getWorklist(
            @RequestParam(defaultValue = "1") Integer companyId,
            @RequestParam(defaultValue = "SAMPLE_RECEIVED") String status) {
        return ResponseEntity.ok(ApiResponse.ok(resultEntryService.getWorklist(companyId, status)));
    }

    @GetMapping("/tests/{testId}")
    @Operation(summary = "Get full test details with analyte observations")
    public ResponseEntity<ApiResponse<PatientLabInvestigation>> getTestDetails(
            @PathVariable("testId") Long testId,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return resultEntryService.getTestDetails(companyId, testId)
                .map(inv -> ResponseEntity.ok(ApiResponse.ok(inv)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/delta-check")
    @Operation(summary = "Calculate delta percentage variance against previous reading")
    public ResponseEntity<ApiResponse<DeltaCheckResult>> checkDelta(
            @RequestParam("currentValue") BigDecimal currentValue,
            @RequestParam("previousValue") BigDecimal previousValue,
            @RequestParam(defaultValue = "20.0") BigDecimal threshold) {
        return ResponseEntity.ok(ApiResponse.ok(deltaCheckService.calculateDelta(currentValue, previousValue, threshold)));
    }
}
