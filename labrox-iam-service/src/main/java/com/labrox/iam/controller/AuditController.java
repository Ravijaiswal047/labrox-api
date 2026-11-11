package com.labrox.iam.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.iam.domain.AuditTrailEvent;
import com.labrox.iam.dto.AuditChainVerificationResponse;
import com.labrox.iam.dto.AuditLogRequest;
import com.labrox.iam.service.AuditTrailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam/audit")
@Tag(name = "21 CFR Part 11 Audit Trail")
public class AuditController {

    private final AuditTrailService auditTrailService;

    public AuditController(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/log")
    @Operation(summary = "Append cryptographic hash-chained audit record")
    public ResponseEntity<ApiResponse<AuditTrailEvent>> logEvent(@Valid @RequestBody AuditLogRequest req) {
        AuditTrailEvent saved = auditTrailService.recordAudit(req);
        return ResponseEntity.ok(ApiResponse.ok("Audit logged successfully", saved));
    }

    @GetMapping("/verify-chain")
    @Operation(summary = "Verify complete cryptographic hash-chain integrity for an entity")
    public ResponseEntity<ApiResponse<AuditChainVerificationResponse>> verifyChain(
            @RequestParam("companyId") Integer companyId,
            @RequestParam("entityName") String entityName,
            @RequestParam("entityId") String entityId) {
        AuditChainVerificationResponse res = auditTrailService.verifyChain(companyId, entityName, entityId);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }
}

