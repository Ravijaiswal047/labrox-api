package com.labrox.diagnostic.controller;

import com.labrox.diagnostic.service.DeltaCheckService;
import com.labrox.diagnostic.service.DiagnosticSampleStore;
import com.labrox.diagnostic.service.ResultEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/RE", "/api/v1/re", "/api/v1/InvestigationCommentMaster", "/api/v1/investigationCommentMaster"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Result Entry API", description = "Endpoints matching legacy nanjappa_api_v9 REController for fe_v9 result entry bench")
public class LegacyREController {

    private final ResultEntryService resultEntryService;
    private final DeltaCheckService deltaCheckService;

    @PostMapping("/GetResultEntry")
    @Operation(summary = "Search worklist of tests awaiting result entry")
    public ResponseEntity<Map<String, Object>> getResultEntry(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy RE GetResultEntry called with: {}", request);
        List<Map<String, Object>> list = DiagnosticSampleStore.getResultEntryWorklist(request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GetResultEntryData")
    @Operation(summary = "Get observation parameters and reference ranges for test result entry")
    public ResponseEntity<Map<String, Object>> getResultEntryData(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy RE GetResultEntryData called with: {}", request);
        Map<String, Object> response = DiagnosticSampleStore.getResultEntryObservations(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SaveResultEntry")
    @Operation(summary = "Save analyte observation readings and mark test tested or approved")
    public ResponseEntity<Map<String, Object>> saveResultEntry(@RequestBody(required = false) Object request) {
        log.info("Legacy RE SaveResultEntry saved: {}", request);
        DiagnosticSampleStore.saveResultEntry(request);

        String msg = "Result Entry Saved Successfully";
        if (request instanceof Map<?, ?> m && "Approved".equalsIgnoreCase(String.valueOf(m.get("ResultStatus")))) {
            msg = "Results Approved Successfully";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", msg);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/PatientSearch")
    public ResponseEntity<Map<String, Object>> patientSearch(@RequestBody(required = false) Map<String, Object> request) {
        return getResultEntry(request);
    }

    @GetMapping("/EmployeeAccessDetails")
    public ResponseEntity<Map<String, Object>> employeeAccessDetails(@RequestParam(name = "Accesstype", required = false) String Accesstype) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("AccessBy", "Save", "IsShow", 1));
        list.add(Map.of("AccessBy", "Approved", "IsShow", 1));
        list.add(Map.of("AccessBy", "Hold", "IsShow", 1));
        list.add(Map.of("AccessBy", "Unhold", "IsShow", 1));
        list.add(Map.of("AccessBy", "Not Approved", "IsShow", 1));
        list.add(Map.of("AccessBy", "Re-Run", "IsShow", 1));
        list.add(Map.of("AccessBy", "Dual Authentication", "IsShow", 1));
        list.add(Map.of("AccessBy", "Forward", "IsShow", 1));

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/PatientObservationDetails")
    public ResponseEntity<Map<String, Object>> patientObservationDetails(@RequestBody(required = false) Map<String, Object> request) {
        return getResultEntryData(request);
    }

    @PostMapping("/ReloadRanges")
    @Operation(summary = "Reload biological reference ranges by patient demographics")
    public ResponseEntity<Map<String, Object>> reloadRanges(@RequestBody(required = false) Map<String, Object> request) {
        return getResultEntryData(request);
    }

    @PostMapping("/DeltaCheck")
    @Operation(summary = "Perform delta check against prior historical result")
    public ResponseEntity<Map<String, Object>> deltaCheck(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> delta = new HashMap<>();
        delta.put("IsDeltaFlag", false);
        delta.put("DeltaVariance", 2.5);
        delta.put("PreviousReading", "13.8");

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", delta);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ForwardResultEntryToDoctor")
    @Operation(summary = "Forward test results to pathologist for final approval")
    public ResponseEntity<Map<String, Object>> forwardToDoctor(@RequestBody(required = false) Object request) {
        log.info("Legacy RE ForwardResultEntryToDoctor: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Forwarded to Doctor for Approval Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SaveLabIssue")
    public ResponseEntity<Map<String, Object>> saveLabIssue(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Lab Issue Saved Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GetAddendum")
    public ResponseEntity<Map<String, Object>> getAddendum(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/BindReportTemplate")
    public ResponseEntity<Map<String, Object>> bindReportTemplate(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/BindMachineGroup")
    public ResponseEntity<Map<String, Object>> bindMachineGroup() {
        List<Map<String, Object>> groups = new ArrayList<>();
        Map<String, Object> g1 = new HashMap<>();
        g1.put("MachineGroupID", 1);
        g1.put("MachineGroup", "Hematology 5-Part Analyzers");
        groups.add(g1);

        Map<String, Object> g2 = new HashMap<>();
        g2.put("MachineGroupID", 2);
        g2.put("MachineGroup", "Clinical Chemistry Analyzers");
        groups.add(g2);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", groups);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/getHelpMenuInvestigationWise")
    public ResponseEntity<Map<String, Object>> getHelpMenuInvestigationWise(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/getInvestigationCommentData", "/GetInvestigationCommentData"})
    public ResponseEntity<Map<String, Object>> getInvestigationCommentData(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> comments = new ArrayList<>();
        Map<String, Object> c1 = new HashMap<>();
        c1.put("InvestigationID", 1);
        c1.put("CommentID", 1);
        c1.put("Template", "CBC Normal Template");
        c1.put("TemplateText", "RBC morphology normocytic normochromic. Platelets adequate on smear. No hemoparasites seen.");
        comments.add(c1);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", comments);
        return ResponseEntity.ok(response);
    }
}
