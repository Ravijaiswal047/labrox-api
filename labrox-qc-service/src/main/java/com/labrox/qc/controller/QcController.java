package com.labrox.qc.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.qc.domain.QcAssayTarget;
import com.labrox.qc.domain.QcControlLot;
import com.labrox.qc.domain.QcRunData;
import com.labrox.qc.dto.LeveyJenningsDataPoint;
import com.labrox.qc.dto.QcRunRequest;
import com.labrox.qc.repository.QcAssayTargetRepository;
import com.labrox.qc.repository.QcControlLotRepository;
import com.labrox.qc.service.QcRunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qc")
@RequiredArgsConstructor
@Tag(name = "Internal Quality Control & Westgard API", description = "Endpoints for QC lots, run ingestion, Westgard multi-rules, and Levey-Jennings charts")
public class QcController {

    private final QcRunService qcRunService;
    private final QcControlLotRepository controlLotRepository;
    private final QcAssayTargetRepository assayTargetRepository;

    @PostMapping("/runs")
    @Operation(summary = "Record QC run measurement, compute Z-score, evaluate Westgard 6-rules, and check analyzer lockout")
    public ResponseEntity<ApiResponse<QcRunData>> recordRun(@RequestBody QcRunRequest request) {
        return qcRunService.recordMeasurement(request)
                .map(run -> ResponseEntity.ok(ApiResponse.ok("QC run evaluated", run)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/levey-jennings/{targetId}")
    @Operation(summary = "Fetch Levey-Jennings chart data points for an assay target")
    public ResponseEntity<ApiResponse<List<LeveyJenningsDataPoint>>> getLeveyJennings(@PathVariable("targetId") Integer targetId) {
        return ResponseEntity.ok(ApiResponse.ok(qcRunService.getLeveyJenningsPoints(targetId)));
    }

    @GetMapping("/lots")
    @Operation(summary = "List active QC control lots")
    public ResponseEntity<ApiResponse<List<QcControlLot>>> getLots(@RequestParam(defaultValue = "1") Integer companyId) {
        return ResponseEntity.ok(ApiResponse.ok(controlLotRepository.findByCompanyIdAndActiveTrue(companyId)));
    }

    @GetMapping("/targets/{lotId}")
    @Operation(summary = "List assay targets (mean & SD) for a control lot")
    public ResponseEntity<ApiResponse<List<QcAssayTarget>>> getTargets(@PathVariable("lotId") Integer lotId) {
        return ResponseEntity.ok(ApiResponse.ok(assayTargetRepository.findByLotId(lotId)));
    }
}
