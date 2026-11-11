package com.labrox.catalog.controller;

import com.labrox.catalog.domain.Doctor;
import com.labrox.catalog.domain.Investigation;
import com.labrox.catalog.dto.FormulaEvaluationRequest;
import com.labrox.catalog.dto.FormulaEvaluationResponse;
import com.labrox.catalog.dto.RangeEvaluationRequest;
import com.labrox.catalog.dto.ReferenceRangeEvaluationResult;
import com.labrox.catalog.service.CatalogService;
import com.labrox.catalog.service.FormulaEvaluatorService;
import com.labrox.catalog.service.ReferenceRangeService;
import com.labrox.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Tag(name = "Master Data & Diagnostic Catalog API", description = "Endpoints for tests, reference intervals, tariff, doctors and formulas")
public class CatalogController {

    private final CatalogService catalogService;
    private final ReferenceRangeService referenceRangeService;
    private final FormulaEvaluatorService formulaEvaluatorService;

    @GetMapping("/investigations")
    @Operation(summary = "Search diagnostic tests in catalog")
    public ResponseEntity<ApiResponse<List<Investigation>>> searchInvestigations(
            @RequestParam(defaultValue = "1") Integer companyId,
            @RequestParam(name = "query", required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.searchInvestigations(companyId, query)));
    }

    @GetMapping("/investigations/{testCode}")
    @Operation(summary = "Get single investigation by test code")
    public ResponseEntity<ApiResponse<Investigation>> getInvestigation(
            @PathVariable("testCode") String testCode,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return catalogService.getInvestigationByCode(companyId, testCode)
                .map(inv -> ResponseEntity.ok(ApiResponse.ok(inv)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctors")
    @Operation(summary = "Search referring doctors")
    public ResponseEntity<ApiResponse<List<Doctor>>> searchDoctors(
            @RequestParam(defaultValue = "1") Integer companyId,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.searchDoctors(companyId, query)));
    }

    @GetMapping("/tariff")
    @Operation(summary = "Lookup tariff price for test and rate type")
    public ResponseEntity<ApiResponse<BigDecimal>> getTariff(
            @RequestParam(defaultValue = "1") Integer companyId,
            @RequestParam("rateTypeId") Integer rateTypeId,
            @RequestParam("investigationId") Integer investigationId) {
        return catalogService.getInvestigationTariff(companyId, rateTypeId, investigationId)
                .map(rate -> ResponseEntity.ok(ApiResponse.ok(rate)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ranges/evaluate")
    @Operation(summary = "Evaluate patient test reading against age & gender reference intervals")
    public ResponseEntity<ApiResponse<ReferenceRangeEvaluationResult>> evaluateRange(
            @RequestBody RangeEvaluationRequest request) {
        ReferenceRangeEvaluationResult result = referenceRangeService.evaluateResult(
                request.getObservationId(),
                request.getGender(),
                request.getAgeDays(),
                request.getValue()
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/formulas/evaluate")
    @Operation(summary = "Evaluate clinical diagnostic formulas (e.g. Indirect Bilirubin, Globulin, A/G Ratio, Anion Gap)")
    public ResponseEntity<ApiResponse<FormulaEvaluationResponse>> evaluateFormula(
            @RequestBody FormulaEvaluationRequest request) {
        BigDecimal result = formulaEvaluatorService.evaluateFormula(request.getFormulaType(), request.getInputs());
        if (result != null) {
            return ResponseEntity.ok(ApiResponse.ok(FormulaEvaluationResponse.builder()
                    .formulaType(request.getFormulaType())
                    .result(result)
                    .success(true)
                    .build()));
        } else {
            return ResponseEntity.ok(ApiResponse.ok(FormulaEvaluationResponse.builder()
                    .formulaType(request.getFormulaType())
                    .success(false)
                    .errorMessage("Invalid inputs or unsupported formula")
                    .build()));
        }
    }
}
