package com.labrox.approval.controller;

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
@RequestMapping("/api/v1/DoctorApproval")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Doctor Approval API", description = "Endpoints matching legacy nanjappa_api_v9 DoctorApprovalController for fe_v9 approval desk")
public class LegacyDoctorApprovalController {

    @PostMapping("/PatientSearch")
    @Operation(summary = "Search patients pending doctor approval")
    public ResponseEntity<Map<String, Object>> patientSearch(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Legacy DoctorApproval PatientSearch: {}", request);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> patient = new HashMap<>();
        patient.put("TestID", 101);
        patient.put("LedgerTransactionID", 1);
        patient.put("BarcodeNo", "BAR20261104001");
        patient.put("PatientID", "LR2026-000043");
        patient.put("PName", "Walk-in Patient");
        patient.put("Age", "35 Y");
        patient.put("Gender", "Male");
        patient.put("InvestigationID", 1);
        patient.put("ItemName", "Complete Blood Count (CBC)");
        patient.put("Department", "Hematology");
        patient.put("Status", 4);
        patient.put("Approved", 0);
        patient.put("Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")));
        list.add(patient);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GetPendingTestList")
    public ResponseEntity<Map<String, Object>> getPendingTestList(@RequestBody(required = false) Map<String, Object> request) {
        return patientSearch(request);
    }

    @PostMapping("/BindCentre")
    public ResponseEntity<Map<String, Object>> bindCentre(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> centres = new ArrayList<>();
        Map<String, Object> c = new HashMap<>();
        c.put("CentreID", 1);
        c.put("Centre", "Main Reference Lab");
        centres.add(c);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", centres);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/BindDepartment")
    public ResponseEntity<Map<String, Object>> bindDepartment() {
        List<Map<String, Object>> depts = new ArrayList<>();
        Map<String, Object> d1 = new HashMap<>();
        d1.put("DepartmentID", 1);
        d1.put("Department", "Hematology");
        depts.add(d1);

        Map<String, Object> d2 = new HashMap<>();
        d2.put("DepartmentID", 2);
        d2.put("Department", "Biochemistry");
        depts.add(d2);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", depts);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/BindTest")
    public ResponseEntity<Map<String, Object>> bindTest(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> tests = new ArrayList<>();
        Map<String, Object> t = new HashMap<>();
        t.put("InvestigationID", 1);
        t.put("TestName", "Complete Blood Count (CBC)");
        tests.add(t);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", tests);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ApplyFormula")
    public ResponseEntity<Map<String, Object>> applyFormula(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Formula Applied Successfully");
        return ResponseEntity.ok(response);
    }
}
