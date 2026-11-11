package com.labrox.iam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Legacy Auxiliary Endpoints Bridge")
public class LegacyAuxiliaryController {

    @RequestMapping(value = {"/api/v1/MachineGroup/SearchMachineBreakdownMarque", "/api/v1/machineGroup/searchMachineBreakdownMarque"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Search machine breakdown marques")
    public ResponseEntity<Map<String, Object>> searchMachineBreakdownMarque() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("data", Collections.emptyList());
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/LanguageMaster/GetLanguages", "/api/v1/languageMaster/getLanguages"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get list of available UI languages")
    public ResponseEntity<Map<String, Object>> getLanguages() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> list = List.of(
            Map.of("LanguageID", 1, "LanguageName", "English", "LanguageCode", "en", "IsActive", 1)
        );
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/LanguageMaster/GetLanguageKeywordNameByLanguageName", "/api/v1/languageMaster/getLanguageKeywordNameByLanguageName"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get localized language dictionary keywords")
    public ResponseEntity<Map<String, Object>> getLanguageKeywords(@RequestParam(value = "LanguageName", required = false) String lang) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/RazorPay/payment", "/api/v1/razorPay/payment"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Initialize Razorpay payment order session")
    public ResponseEntity<Map<String, Object>> initRazorpayPayment(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Payment session active");
        response.put("data", Collections.emptyMap());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/Designation/getDesignationDataByEmployeeId", "/api/v1/designation/getDesignationDataByEmployeeId"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get designations for an employee")
    public ResponseEntity<Map<String, Object>> getDesignationDataByEmployeeId(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> list = List.of(
            Map.of("DesignationID", 1, "DesignationName", "Super Administrator", "value", 1, "label", "Super Administrator")
        );
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/Accounts/CheckOutstanding", "/api/v1/accounts/checkOutstanding"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Check client/patient outstanding balance")
    public ResponseEntity<Map<String, Object>> checkOutstanding(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "0");
        response.put("data", 0.0);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/ReferalLab/GetReferalLabDropdwon", "/api/v1/referalLab/getReferalLabDropdwon"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get referral laboratories dropdown")
    public ResponseEntity<Map<String, Object>> getReferalLabDropdown(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/FieldBoyMaster/BindFieldBoy", "/api/v1/fieldBoyMaster/bindFieldBoy"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get field collection boys dropdown")
    public ResponseEntity<Map<String, Object>> bindFieldBoy(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/CommonController/GetDashboard_Data", "/api/v1/commonController/getDashboard_Data"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get main dashboard KPI charts and statistics")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/MachineGroup/BindMachineName", "/api/v1/machineGroup/bindMachineName"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get machines dropdown for laboratory analyzers")
    public ResponseEntity<Map<String, Object>> bindMachineName(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> list = List.of(
            Map.of("MachineId", 1, "MachineName", "Cobas 6000", "label", "Cobas 6000", "value", 1),
            Map.of("MachineId", 2, "MachineName", "Sysmex XN-1000", "label", "Sysmex XN-1000", "value", 2),
            Map.of("MachineId", 3, "MachineName", "Architect i2000", "label", "Architect i2000", "value", 3)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/HistoCyto/BindStain", "/api/v1/histoCyto/bindStain"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get histopathology stains dropdown")
    public ResponseEntity<Map<String, Object>> bindStain(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> list = List.of(
            Map.of("ID", 1, "OrganName", "H&E", "label", "H&E", "value", 1),
            Map.of("ID", 2, "OrganName", "PAP", "label", "PAP", "value", 2),
            Map.of("ID", 3, "OrganName", "Giemsa", "label", "Giemsa", "value", 3),
            Map.of("ID", 4, "OrganName", "PAS", "label", "PAS", "value", 4)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/Lab/getFilterTableReprintData", "/api/v1/lab/getFilterTableReprintData"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get dynamic filter and column config")
    public ResponseEntity<Map<String, Object>> getFilterTableReprintData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/BuildingBlock/GetList/{id}", "/api/v1/BuildingBlock/GetList",
                            "/api/v1/buildingBlock/getList/{id}", "/api/v1/buildingBlock/getList"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get result entry building blocks list")
    public ResponseEntity<Map<String, Object>> getBuildingBlockList(@PathVariable(required = false) String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/DignosticMaster/GetDignosticDDL", "/api/v1/dignosticMaster/getDignosticDDL"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get diagnostic department category dropdown")
    public ResponseEntity<Map<String, Object>> getDignosticDDL(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> list = List.of(
            Map.of("Dignomasterid", 1, "DignoName", "Pathology", "label", "Pathology", "value", 1),
            Map.of("Dignomasterid", 2, "DignoName", "Radiology", "label", "Radiology", "value", 2)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/Department/getDepartment", "/api/v1/Department/GetDepartment",
                            "/api/v1/department/getDepartment"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get list of laboratory clinical departments")
    public ResponseEntity<Map<String, Object>> getDepartment(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> list = List.of(
            Map.of("DepartmentID", 1, "Department", "Biochemistry", "label", "Biochemistry", "value", 1),
            Map.of("DepartmentID", 2, "Department", "Hematology", "label", "Hematology", "value", 2),
            Map.of("DepartmentID", 3, "Department", "Microbiology", "label", "Microbiology", "value", 3),
            Map.of("DepartmentID", 4, "Department", "Serology", "label", "Serology", "value", 4),
            Map.of("DepartmentID", 5, "Department", "Histopathology", "label", "Histopathology", "value", 5),
            Map.of("DepartmentID", 6, "Department", "Clinical Pathology", "label", "Clinical Pathology", "value", 6)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }
}
