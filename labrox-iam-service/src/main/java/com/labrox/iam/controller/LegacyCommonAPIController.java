package com.labrox.iam.controller;

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
@RequestMapping("/api/v1/CommonAPI")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Common API", description = "Endpoints matching legacy nanjappa_api_v9 CommonAPIController for datetime and global centers")
public class LegacyCommonAPIController {

    @GetMapping("/GetDateTimeNow")
    @Operation(summary = "Get current synchronized server datetime")
    public ResponseEntity<Map<String, Object>> getDateTimeNow() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/BindAllCenter")
    @Operation(summary = "Bind all centers for selection")
    public ResponseEntity<Map<String, Object>> bindAllCenter() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> c = new HashMap<>();
        c.put("CentreID", 1);
        c.put("Centre", "Main Reference Lab");
        c.put("CentreCode", "MRL");
        list.add(c);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }
}
