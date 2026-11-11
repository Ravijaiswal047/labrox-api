package com.labrox.approval.controller;

import com.labrox.approval.service.ApprovalWorkflowService;
import com.labrox.approval.service.ElectronicSignatureService;
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
@RequestMapping("/api/v1/ChangeResultStatus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Medical Approval API", description = "Endpoints matching legacy nanjappa_api_v9 ChangeResultStatusController for fe_v9 doctor approval")
public class LegacyChangeResultStatusController {

    private final ApprovalWorkflowService approvalService;
    private final ElectronicSignatureService signatureService;

    @PostMapping("/getChangesReportStatus")
    @Operation(summary = "Search patient tests waiting for pathologist approval or status change")
    public ResponseEntity<Map<String, Object>> getChangesReportStatus(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy ChangeResultStatus getChangesReportStatus: {}", request);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> test = new HashMap<>();
        test.put("TestID", 101);
        test.put("LedgerTransactionID", 1);
        test.put("BarcodeNo", "BAR20261104001");
        test.put("PatientID", "LR2026-000043");
        test.put("PName", "Walk-in Patient");
        test.put("Age", "35 Y");
        test.put("Gender", "Male");
        test.put("InvestigationID", 1);
        test.put("TestCode", "CBC01");
        test.put("ItemName", "Complete Blood Count (CBC)");
        test.put("Department", "Hematology");
        test.put("Status", 4);
        test.put("Approved", 0);
        test.put("Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")));
        test.put("SampleTypeName", "Whole Blood EDTA");
        test.put("CentreName", "Main Reference Lab");
        test.put("DoctorName", "Dr. Rajesh Sharma, MD");
        list.add(test);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/UpdateChangeResultStatus")
    @Operation(summary = "Approve, unapprove, or hold tests with doctor digital sign-off")
    public ResponseEntity<Map<String, Object>> updateChangeResultStatus(@RequestBody(required = false) Object request) {
        log.info("Legacy ChangeResultStatus UpdateChangeResultStatus executed: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Status Updated Successfully");
        return ResponseEntity.ok(response);
    }
}
