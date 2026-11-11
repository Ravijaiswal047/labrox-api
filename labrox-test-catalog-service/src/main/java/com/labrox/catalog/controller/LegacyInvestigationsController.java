package com.labrox.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/Investigations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Investigations Master API", description = "Endpoints matching legacy nanjappa_api_v9 InvestigationsController")
public class LegacyInvestigationsController {

    @RequestMapping(value = {"/GetInvestigations", "/getInvestigations"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Search master test investigation catalog")
    public ResponseEntity<Map<String, Object>> getInvestigations(@RequestBody(required = false) Map<String, Object> request) {
        List<Map<String, Object>> list = getCatalogList();
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/BindInvestigationList")
    @Operation(summary = "Bind test investigation list for billing dropdowns")
    public ResponseEntity<Map<String, Object>> bindInvestigationList(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> list = getCatalogList();
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> getCatalogList() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> t1 = new HashMap<>();
        t1.put("InvestigationID", 1);
        t1.put("TestName", "Complete Blood Count (CBC)");
        t1.put("TestCode", "CBC01");
        t1.put("DepartmentID", 1);
        t1.put("DepartmentName", "Hematology");
        t1.put("SampleTypeID", 1);
        t1.put("SampleTypeName", "Whole Blood EDTA");
        t1.put("Rate", new BigDecimal("450.00"));
        t1.put("IsActive", 1);
        list.add(t1);

        Map<String, Object> t2 = new HashMap<>();
        t2.put("InvestigationID", 2);
        t2.put("TestName", "Lipid Profile Comprehensive");
        t2.put("TestCode", "LIP02");
        t2.put("DepartmentID", 2);
        t2.put("DepartmentName", "Biochemistry");
        t2.put("SampleTypeID", 2);
        t2.put("SampleTypeName", "Serum Gel Tube");
        t2.put("Rate", new BigDecimal("850.00"));
        t2.put("IsActive", 1);
        list.add(t2);

        Map<String, Object> t3 = new HashMap<>();
        t3.put("InvestigationID", 3);
        t3.put("TestName", "Fasting Blood Glucose (FBS)");
        t3.put("TestCode", "GLU03");
        t3.put("DepartmentID", 2);
        t3.put("DepartmentName", "Biochemistry");
        t3.put("SampleTypeID", 3);
        t3.put("SampleTypeName", "Fluoride Plasma");
        t3.put("Rate", new BigDecimal("120.00"));
        t3.put("IsActive", 1);
        list.add(t3);

        Map<String, Object> t4 = new HashMap<>();
        t4.put("InvestigationID", 4);
        t4.put("TestName", "Liver Function Test (LFT)");
        t4.put("TestCode", "LFT04");
        t4.put("DepartmentID", 2);
        t4.put("DepartmentName", "Biochemistry");
        t4.put("SampleTypeID", 2);
        t4.put("SampleTypeName", "Serum Gel Tube");
        t4.put("Rate", new BigDecimal("750.00"));
        t4.put("IsActive", 1);
        list.add(t4);

        Map<String, Object> t5 = new HashMap<>();
        t5.put("InvestigationID", 5);
        t5.put("TestName", "Kidney Function Test (KFT / RFT)");
        t5.put("TestCode", "KFT05");
        t5.put("DepartmentID", 2);
        t5.put("DepartmentName", "Biochemistry");
        t5.put("SampleTypeID", 2);
        t5.put("SampleTypeName", "Serum Gel Tube");
        t5.put("Rate", new BigDecimal("650.00"));
        t5.put("IsActive", 1);
        list.add(t5);

        return list;
    }
}
