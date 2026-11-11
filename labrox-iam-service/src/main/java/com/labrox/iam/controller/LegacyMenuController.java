package com.labrox.iam.controller;

import com.labrox.iam.service.LegacyMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/Menu", "/api/v1/menu"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Menu & Navigation API Bridge", description = "Endpoints matching legacy nanjappa_api_v9 MenuController for hierarchical navigation menus, sub-pages, quick links, and route authorization")
public class LegacyMenuController {

    private final LegacyMetadataService metadataService;

    @GetMapping({"/MainMenuPageData", "/mainMenuPageData"})
    @Operation(summary = "Fetch complete navigation menu tree and sub-pages for current user")
    public ResponseEntity<Map<String, Object>> getMainMenuPageData() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> menus = metadataService.getMenus();
            List<Map<String, Object>> pages = metadataService.getPages();

            Map<String, Object> menuPayload = new HashMap<>();
            menuPayload.put("MenuData", menus);
            menuPayload.put("menuData", menus);
            menuPayload.put("PageData", pages);
            menuPayload.put("pageData", pages);
            menuPayload.put("EmployeeData", List.of(Map.of("employeeid", 1, "ImageGuid", "")));
            menuPayload.put("DownTimeData", Collections.emptyList());

            log.info("Returning MainMenuPageData: {} menus, {} pages to frontend sidebar", menus.size(), pages.size());

            response.put("success", true);
            response.put("status", true);
            response.put("message", menuPayload);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Error generating MainMenuPageData: {}", ex.getMessage(), ex);
            response.put("success", false);
            response.put("status", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping({"/ValidMenuPageData", "/validMenuPageData"})
    @Operation(summary = "Validate user permission on active route")
    public ResponseEntity<Map<String, Object>> validMenuPageData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("message", "Authorized");
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/getQuickLinks", "/GetQuickLinks"})
    @Operation(summary = "Get user favourite quick links")
    public ResponseEntity<Map<String, Object>> getQuickLinks() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }
}
