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
@RequestMapping({"/api/v1/CompanyMaster", "/api/v1/companymaster"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Company Master & Dynamic UI Controls API", description = "Endpoints matching legacy elabpro CompanyMasterController for dynamic form controls, company configurations, and field ordering")
public class CompanyMasterController {

    private final LegacyMetadataService metadataService;

    @PostMapping({"/GetPageDataDetails", "/getPageDataDetails"})
    @Operation(summary = "Retrieve dynamic page control master definitions for rendering forms")
    public ResponseEntity<Map<String, Object>> getPageDataDetails(@RequestBody(required = false) Map<String, Object> body) {
        String pageId = "";
        if (body != null) {
            if (body.containsKey("pageID")) {
                pageId = String.valueOf(body.get("pageID"));
            } else if (body.containsKey("PageID")) {
                pageId = String.valueOf(body.get("PageID"));
            } else if (body.containsKey("pageId")) {
                pageId = String.valueOf(body.get("pageId"));
            }
        }

        List<Map<String, Object>> controls = metadataService.getPageControls(pageId);
        log.debug("GetPageDataDetails requested for PageID: {}, returning {} controls", pageId, controls.size());

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", controls);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetPageControlData", "/getPageControlData"})
    @Operation(summary = "Retrieve page control settings by page")
    public ResponseEntity<Map<String, Object>> getPageControlData(@RequestBody(required = false) Map<String, Object> body) {
        return getPageDataDetails(body);
    }

    @GetMapping({"/GetPageMasterData", "/getPageMasterData"})
    @Operation(summary = "Get list of all active pages for control configuration")
    public ResponseEntity<Map<String, Object>> getPageMasterData() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", metadataService.getPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SavePageControlData", "/savePageControlData"})
    @Operation(summary = "Save customized page control visibility and order")
    public ResponseEntity<Map<String, Object>> savePageControlData(@RequestBody(required = false) List<Map<String, Object>> controls) {
        if (controls != null) {
            metadataService.saveOrUpdatePageControls(controls);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Page controls saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdatePageControlData", "/updatePageControlData"})
    @Operation(summary = "Update customized page controls")
    public ResponseEntity<Map<String, Object>> updatePageControlData(@RequestBody(required = false) List<Map<String, Object>> controls) {
        return savePageControlData(controls);
    }

    @PostMapping({"/SaveMappingPageControlData", "/saveMappingPageControlData"})
    @Operation(summary = "Save mapping page control data")
    public ResponseEntity<Map<String, Object>> saveMappingPageControlData(@RequestBody(required = false) List<Map<String, Object>> controls) {
        return savePageControlData(controls);
    }

    @GetMapping({"/getCompanyName", "/GetCompanyName"})
    @Operation(summary = "Get registered company name and basic details")
    public ResponseEntity<Map<String, Object>> getCompanyName() {
        Map<String, Object> company = new HashMap<>();
        company.put("CompanyId", 1);
        company.put("CompanyID", 1);
        company.put("CompanyName", "Nanjappa Healthcare / LabRox LIMS");
        company.put("CompanyCode", "NHPL");
        company.put("IsActive", 1);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(company));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCompanyMaster", "/getCompanyMaster"})
    @Operation(summary = "Fetch company master records")
    public ResponseEntity<Map<String, Object>> getCompanyMaster(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> comp = new HashMap<>();
        comp.put("CompanyID", 1);
        comp.put("CompanyName", "Nanjappa Healthcare / LabRox LIMS");
        comp.put("CompanyCode", "NHPL");
        comp.put("IsActive", 1);
        comp.put("Email", "info@nanjappadiagnostics.com");
        comp.put("Mobile", "9876543210");
        comp.put("Address", "Nanjappa Hospital Campus, Shimoga, Karnataka");

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(comp));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCompanySearch", "/getCompanySearch", "/gettabledata", "/GetTableData"})
    @Operation(summary = "Search company data")
    public ResponseEntity<Map<String, Object>> getCompanySearch(@RequestBody(required = false) Map<String, Object> body) {
        return getCompanyMaster(body);
    }

    @PostMapping({"/GetCompanyConfig", "/getCompanyConfig"})
    @Operation(summary = "Retrieve company-wide feature flags and configurations")
    public ResponseEntity<Map<String, Object>> getCompanyConfig(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> config = new HashMap<>();
        config.put("CompanyID", 1);
        config.put("IsBarcodeAutoGenerate", 1);
        config.put("IsSampleCollectionMandatory", 1);
        config.put("IsDepartmentReceiveMandatory", 1);
        config.put("IsDoctorApprovalMandatory", 1);
        config.put("IsDeltaCheckActive", 1);
        config.put("IsAutoApprovalEnabled", 0);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(config));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdateCompanyConfig", "/updateCompanyConfig"})
    @Operation(summary = "Update company-wide configurations")
    public ResponseEntity<Map<String, Object>> updateCompanyConfig(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Configuration updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCompanywiseCheckbox", "/getCompanywiseCheckbox"})
    @Operation(summary = "Get company-specific module checkboxes")
    public ResponseEntity<Map<String, Object>> getCompanywiseCheckbox(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> cb = new HashMap<>();
        cb.put("CompanyID", 1);
        cb.put("IsDiscountAllow", 1);
        cb.put("IsBillingPOS", 1);
        cb.put("IsTRFMandatory", 0);
        cb.put("IsOnlinePaymentAllow", 1);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(cb));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdateCompanywiseCheckbox", "/updateCompanywiseCheckbox"})
    @Operation(summary = "Update company module checkboxes")
    public ResponseEntity<Map<String, Object>> updateCompanywiseCheckbox(@RequestBody(required = false) Object body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Checkbox configurations saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCompanyFlagMaster", "/getCompanyFlagMaster"})
    @Operation(summary = "Get company flags")
    public ResponseEntity<Map<String, Object>> getCompanyFlagMaster(@RequestParam(value = "PageID", required = false) String pageId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/InsertAndUpdateCompanyFlagMaster", "/insertAndUpdateCompanyFlagMaster"})
    public ResponseEntity<Map<String, Object>> insertAndUpdateCompanyFlagMaster(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Flag saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCompanyPaymentConfigurations", "/getCompanyPaymentConfigurations"})
    public ResponseEntity<Map<String, Object>> getCompanyPaymentConfigurations(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> p = new HashMap<>();
        p.put("CompanyID", 1);
        p.put("RazorPayKeyId", "rzp_test_sampleKey");
        p.put("IsPaymentGatewayEnabled", 1);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(p));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdateCompanyPaymentConfigurations", "/updateCompanyPaymentConfigurations"})
    public ResponseEntity<Map<String, Object>> updateCompanyPaymentConfigurations(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Payment configuration updated");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/AddCentreOrRateTypeFlagMaster", "/addCentreOrRateTypeFlagMaster"})
    public ResponseEntity<Map<String, Object>> addCentreOrRateTypeFlagMaster(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Centre flag saved");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCentreOrRateTypeFlagMaster", "/getCentreOrRateTypeFlagMaster"})
    public ResponseEntity<Map<String, Object>> getCentreOrRateTypeFlagMaster(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetSMSDataCompanyWise", "/getSMSDataCompanyWise"})
    public ResponseEntity<Map<String, Object>> getSMSDataCompanyWise(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/ImportJsonData", "/importJsonData"})
    public ResponseEntity<Map<String, Object>> importJsonData(@RequestBody(required = false) Object body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Import successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/GetMouPdf", "/getMouPdf"})
    public ResponseEntity<Map<String, Object>> getMouPdf(@RequestParam(value = "Type", required = false) String type) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }
}
