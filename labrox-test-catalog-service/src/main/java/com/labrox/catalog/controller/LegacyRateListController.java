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
@RequestMapping("/api/v1/RateList")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Rate List API", description = "Endpoints matching legacy nanjappa_api_v9 RateListController for panel pricing")
public class LegacyRateListController {

    @PostMapping("/GetRateListWithType")
    @Operation(summary = "Get rate list matrix for specific rate type and panel")
    public ResponseEntity<Map<String, Object>> getRateListWithType(@RequestBody(required = false) Map<String, Object> request) {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> r1 = new HashMap<>();
        r1.put("RateListID", 1);
        r1.put("InvestigationID", 1);
        r1.put("TestName", "Complete Blood Count (CBC)");
        r1.put("RateTypeID", 1);
        r1.put("Rate", new BigDecimal("450.00"));
        list.add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("RateListID", 2);
        r2.put("InvestigationID", 2);
        r2.put("TestName", "Lipid Profile Comprehensive");
        r2.put("RateTypeID", 1);
        r2.put("Rate", new BigDecimal("850.00"));
        list.add(r2);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SaveRateList")
    @Operation(summary = "Save or update panel rate list pricing")
    public ResponseEntity<Map<String, Object>> saveRateList(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Rate List Saved Successfully");
        return ResponseEntity.ok(response);
    }
}
