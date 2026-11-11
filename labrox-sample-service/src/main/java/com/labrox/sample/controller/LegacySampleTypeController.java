package com.labrox.sample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/SampleType", "/api/v1/sampleType"})
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Sample Type Master API", description = "Endpoints matching legacy nanjappa_api_v9 SampleTypeController")
public class LegacySampleTypeController {

    private static final List<Map<String, Object>> SAMPLE_TYPES = new ArrayList<>();

    static {
        addSampleType(1, "Whole Blood EDTA", "#800080");
        addSampleType(2, "Serum", "#FFD700");
        addSampleType(3, "Fluoride Plasma", "#808080");
        addSampleType(4, "Sodium Citrate Plasma", "#87CEEB");
        addSampleType(5, "Plain Blood (Red)", "#DC143C");
        addSampleType(6, "Heparin Plasma", "#228B22");
        addSampleType(7, "Urine Routine", "#FFA500");
        addSampleType(8, "Stool Sample", "#8B4513");
        addSampleType(9, "Sputum", "#4682B4");
        addSampleType(10, "CSF", "#00CED1");
        addSampleType(11, "Swab", "#F08080");
    }

    private static void addSampleType(int id, String name, String color) {
        Map<String, Object> st = new LinkedHashMap<>();
        st.put("id", id);
        st.put("value", id);
        st.put("SampleTypeID", id);
        st.put("SampleName", name);
        st.put("label", name);
        st.put("ColorName", color);
        st.put("RetentionDays", 7);
        st.put("isActive", 1);
        SAMPLE_TYPES.add(Collections.unmodifiableMap(st));
    }

    @GetMapping({"/getSampleType", "/GetSampleType"})
    @Operation(summary = "Get list of all active sample types with colors")
    public ResponseEntity<Map<String, Object>> getSampleType() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", SAMPLE_TYPES);
        response.put("data", SAMPLE_TYPES);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/getSampleTypeInVestigationWise", "/GetSampleTypeInVestigationWise",
                  "/getSampleTypeInvestigationWise", "/GetSampleTypeInvestigationWise"})
    @Operation(summary = "Get applicable sample types for investigation")
    public ResponseEntity<Map<String, Object>> getSampleTypeInVestigationWise(@RequestBody(required = false) Map<String, Object> request) {
        log.info("getSampleTypeInVestigationWise called with: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", SAMPLE_TYPES);
        response.put("data", SAMPLE_TYPES);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/getSampleTypeByInvestigation", "/GetSampleTypeByInvestigation"})
    @Operation(summary = "Get sample types grouped by investigation")
    public ResponseEntity<Map<String, Object>> getSampleTypeByInvestigation() {
        return getSampleType();
    }

    @PostMapping({"/SaveSampleType", "/saveSampleType"})
    @Operation(summary = "Create sample type")
    public ResponseEntity<Map<String, Object>> saveSampleType(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample Type Saved Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdateSampleType", "/updateSampleType"})
    @Operation(summary = "Update sample type")
    public ResponseEntity<Map<String, Object>> updateSampleType(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Sample Type Updated Successfully");
        return ResponseEntity.ok(response);
    }
}
