package com.labrox.approval.controller;

import com.labrox.approval.domain.AmendmentHistory;
import com.labrox.approval.domain.ElectronicSignature;
import com.labrox.approval.domain.PanicCallLog;
import com.labrox.approval.domain.TestApprovalLog;
import com.labrox.approval.dto.AmendmentRequest;
import com.labrox.approval.dto.ApprovalRequest;
import com.labrox.approval.dto.HoldRerunRequest;
import com.labrox.approval.dto.PanicCallRequest;
import com.labrox.approval.repository.AmendmentHistoryRepository;
import com.labrox.approval.repository.ElectronicSignatureRepository;
import com.labrox.approval.repository.TestApprovalLogRepository;
import com.labrox.approval.service.ApprovalWorkflowService;
import com.labrox.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/approval")
@RequiredArgsConstructor
@Tag(name = "Workflow Approvals & 21 CFR Part 11 Electronic Signature API", description = "Endpoints for doctor approval, digital signatures, holds, reruns, and regulated amendments")
public class ApprovalController {

    private final ApprovalWorkflowService approvalService;
    private final ElectronicSignatureRepository signatureRepository;
    private final AmendmentHistoryRepository amendmentRepository;
    private final TestApprovalLogRepository logRepository;

    @PostMapping("/approve")
    @Operation(summary = "Approve diagnostic test with 21 CFR Part 11 electronic signature")
    public ResponseEntity<ApiResponse<TestApprovalLog>> approveTest(@RequestBody ApprovalRequest request) {
        return approvalService.approveInvestigation(request)
                .map(log -> ResponseEntity.ok(ApiResponse.ok("Investigation approved successfully", log)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/hold")
    @Operation(summary = "Place investigation on HOLD")
    public ResponseEntity<ApiResponse<TestApprovalLog>> holdTest(@RequestBody HoldRerunRequest request) {
        return approvalService.holdInvestigation(request)
                .map(log -> ResponseEntity.ok(ApiResponse.ok("Investigation placed on HOLD", log)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rerun")
    @Operation(summary = "Trigger test rerun and return to laboratory bench worklist")
    public ResponseEntity<ApiResponse<TestApprovalLog>> rerunTest(@RequestBody HoldRerunRequest request) {
        return approvalService.rerunInvestigation(request)
                .map(log -> ResponseEntity.ok(ApiResponse.ok("Investigation ordered for RERUN", log)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/amend")
    @Operation(summary = "Execute 21 CFR Part 11 regulated amendment: invalidates previous signature, updates value, records audit diff")
    public ResponseEntity<ApiResponse<AmendmentHistory>> amendResult(@RequestBody AmendmentRequest request) {
        return approvalService.amendResult(request)
                .map(amend -> ResponseEntity.ok(ApiResponse.ok("Result amended and re-signed successfully", amend)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/panic-call")
    @Operation(summary = "Log critical panic alarm telephone escalation to physician/ward")
    public ResponseEntity<ApiResponse<PanicCallLog>> logPanicCall(@RequestBody PanicCallRequest request) {
        PanicCallLog log = approvalService.logPanicCall(request);
        return ResponseEntity.ok(ApiResponse.ok("Panic call notification logged", log));
    }

    @GetMapping("/signatures/{testId}")
    @Operation(summary = "Get valid electronic signature for test")
    public ResponseEntity<ApiResponse<ElectronicSignature>> getSignature(
            @PathVariable("testId") Long testId,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return signatureRepository.findByCompanyIdAndTestIdAndValidTrue(companyId, testId)
                .map(sig -> ResponseEntity.ok(ApiResponse.ok(sig)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/amendments/{testId}")
    @Operation(summary = "Get amendment history trail for test")
    public ResponseEntity<ApiResponse<List<AmendmentHistory>>> getAmendments(
            @PathVariable Long testId,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return ResponseEntity.ok(ApiResponse.ok(amendmentRepository.findByCompanyIdAndTestIdOrderByAmendedAtDesc(companyId, testId)));
    }

    @GetMapping("/logs/{testId}")
    @Operation(summary = "Get workflow approval state transition history")
    public ResponseEntity<ApiResponse<List<TestApprovalLog>>> getLogs(
            @PathVariable Long testId,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return ResponseEntity.ok(ApiResponse.ok(logRepository.findByCompanyIdAndTestIdOrderByCreatedAtDesc(companyId, testId)));
    }
}
