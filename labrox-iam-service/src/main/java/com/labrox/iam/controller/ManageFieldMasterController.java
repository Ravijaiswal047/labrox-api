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
@RequestMapping({"/api/v1/ManageFieldMaster", "/api/v1/managefieldmaster"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Manage Field Master API", description = "Endpoints matching legacy elabpro ManageFieldMasterController for dynamic field visibility (BedNo, Source, OPDIPD, Vip&Masking, etc.) per visit type")
public class ManageFieldMasterController {

    private final LegacyMetadataService metadataService;

    @PostMapping({"/getAllManageFieldMasterData", "/GetAllManageFieldMasterData"})
    @Operation(summary = "Get dynamic visibility and mandatory flags for visit type fields")
    public ResponseEntity<Map<String, Object>> getAllManageFieldMasterData(@RequestBody(required = false) Map<String, Object> body) {
        Integer visitTypeId = null;
        if (body != null && body.containsKey("VisitTypeID")) {
            Object vt = body.get("VisitTypeID");
            if (vt instanceof Number) {
                visitTypeId = ((Number) vt).intValue();
            } else if (vt != null) {
                try {
                    visitTypeId = Integer.parseInt(String.valueOf(vt));
                } catch (NumberFormatException ignored) {}
            }
        }

        List<Map<String, Object>> fields = metadataService.getManageFields(visitTypeId);
        log.debug("getAllManageFieldMasterData for VisitTypeID: {}, returning {} fields", visitTypeId, fields.size());

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", fields);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SaveManageFieldMasterData", "/saveManageFieldMasterData"})
    @Operation(summary = "Save field master visibility setting")
    public ResponseEntity<Map<String, Object>> saveManageFieldMasterData(@RequestBody(required = false) Map<String, Object> body) {
        if (body != null) {
            metadataService.saveOrUpdateManageFields(body);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Field visibility saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdateManageFieldMasterData", "/updateManageFieldMasterData"})
    @Operation(summary = "Update field master visibility setting")
    public ResponseEntity<Map<String, Object>> updateManageFieldMasterData(@RequestBody(required = false) Map<String, Object> body) {
        return saveManageFieldMasterData(body);
    }
}
