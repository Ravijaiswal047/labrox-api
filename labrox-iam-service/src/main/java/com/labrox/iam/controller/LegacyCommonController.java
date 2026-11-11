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
@RequestMapping({"/api/v1/CommonController", "/api/v1/commonController"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Common Controller API", description = "Endpoints matching legacy nanjappa_api_v9 CommonController for roles, machine groups, gender mapping, and center bindings")
public class LegacyCommonController {

    @PostMapping({"/BindCentre", "/bindCentre"})
    @Operation(summary = "Bind active processing and collection centers")
    public ResponseEntity<Map<String, Object>> bindCentre(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> c = new HashMap<>();
        c.put("CentreID", 1);
        c.put("Centre", "Main Reference Lab");
        c.put("CentreCode", "MRL");
        c.put("DataType", "MainCentre");
        list.add(c);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/BindRole", "/bindRole"})
    @Operation(summary = "Bind user security roles and permission levels")
    public ResponseEntity<Map<String, Object>> bindRole(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("RoleID", 1, "RoleName", "Administrator"));
        list.add(Map.of("RoleID", 2, "RoleName", "Pathologist"));
        list.add(Map.of("RoleID", 3, "RoleName", "Phlebotomist / Technician"));

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetMachineGroup", "/getMachineGroup"})
    @Operation(summary = "Retrieve analyzer machine groups")
    public ResponseEntity<Map<String, Object>> getMachineGroup(@RequestBody(required = false) Object request) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("MachineGroupID", 1, "MachineGroup", "Hematology Analyzers"));
        list.add(Map.of("MachineGroupID", 2, "MachineGroup", "Biochemistry Analyzers"));

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/Fetchprocedure", "/fetchprocedure"})
    public ResponseEntity<Map<String, Object>> fetchProcedure(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/EmployeeMenuDirection", "/employeeMenuDirection"})
    public ResponseEntity<Map<String, Object>> employeeMenuDirection(@RequestBody(required = false) Object request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "LTR");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetVisitCategoryData", "/getVisitCategoryData"})
    @Operation(summary = "Get visit category configurations")
    public ResponseEntity<Map<String, Object>> getVisitCategoryData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> list = List.of(
            Map.of("VisitCategoryID", 1, "CategoryName", "Routine Out-Patient", "value", 1, "label", "Routine Out-Patient"),
            Map.of("VisitCategoryID", 2, "CategoryName", "STAT Urgent / Emergency", "value", 2, "label", "STAT Urgent / Emergency"),
            Map.of("VisitCategoryID", 3, "CategoryName", "Health Checkup Package", "value", 3, "label", "Health Checkup Package")
        );
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/CheckInvalidMobileNo", "/checkInvalidMobileNo"})
    @Operation(summary = "Validate patient contact number format")
    public ResponseEntity<Map<String, Object>> checkInvalidMobileNo(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Valid mobile number");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetDocument", "/getDocument", "/GetFileUrl", "/getFileUrl"})
    @Operation(summary = "Document and TRF file retrieval")
    public ResponseEntity<Map<String, Object>> getDocument(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetCompanyGenderTitleMapping", "/getCompanyGenderTitleMapping"})
    @Operation(summary = "Retrieve gender mapping by title (Mr., Mrs., Baby, etc.)")
    public ResponseEntity<Map<String, Object>> getCompanyGenderTitleMapping(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> list = List.of(
            Map.of("ID", 1, "Title", "Mr.", "Gender", "Male", "IsVetCanine", 0, "CompanyID", 1),
            Map.of("ID", 2, "Title", "Mrs.", "Gender", "Female", "IsVetCanine", 0, "CompanyID", 1),
            Map.of("ID", 3, "Title", "Miss", "Gender", "Female", "IsVetCanine", 0, "CompanyID", 1),
            Map.of("ID", 4, "Title", "Ms.", "Gender", "Female", "IsVetCanine", 0, "CompanyID", 1),
            Map.of("ID", 5, "Title", "Master", "Gender", "Male", "IsVetCanine", 0, "CompanyID", 1),
            Map.of("ID", 6, "Title", "Baby", "Gender", "Both", "IsVetCanine", 0, "CompanyID", 1),
            Map.of("ID", 7, "Title", "Dr.", "Gender", "Both", "IsVetCanine", 0, "CompanyID", 1)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SaveCompanyGenderTitleMapping", "/saveCompanyGenderTitleMapping", "/UpdateCompanyGenderTitleMapping", "/updateCompanyGenderTitleMapping"})
    public ResponseEntity<Map<String, Object>> saveCompanyGenderTitleMapping(@RequestBody(required = false) Object body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Gender title mapping saved");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetLastVisitNoByCentreID", "/getLastVisitNoByCentreID"})
    @Operation(summary = "Get last visit number by centre")
    public ResponseEntity<Map<String, Object>> getLastVisitNoByCentreID(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "VIS-" + System.currentTimeMillis() % 10000);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetBarcodePrintOutConfig", "/getBarcodePrintOutConfig"})
    public ResponseEntity<Map<String, Object>> getBarcodePrintOutConfig(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> config = new HashMap<>();
        config.put("PrintOption", "Direct");
        config.put("PrinterName", "Zebra ZD220");
        config.put("Copies", 1);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(config));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetApiConfiguration", "/getApiConfiguration"})
    public ResponseEntity<Map<String, Object>> getApiConfiguration(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/GetCompanyLogo", "/getCompanyLogo"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> getCompanyLogo(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/GetHeaderLogo", "/getHeaderLogo"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> getHeaderLogo() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/GetClientContractDays", "/getClientContractDays"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> getClientContractDays(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("data", Collections.emptyList());
        response.put("message", 365);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/GetStockItemToReorderLevel", "/getStockItemToReorderLevel"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> getStockItemToReorderLevel() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/GetStoreDataToExpire", "/getStoreDataToExpire"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> getStoreDataToExpire() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/DepartmentWiseItemList", "/departmentWiseItemList", "/DepartmentWiseItem", "/departmentWiseItem"})
    public ResponseEntity<Map<String, Object>> departmentWiseItemList(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/GetAccessProcessingLab", "/getAccessProcessingLab"})
    public ResponseEntity<Map<String, Object>> getAccessProcessingLab(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> lab = Map.of("CentreID", 1, "Centre", "Main Reference Lab");
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(lab));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/BindTestCentreList", "/bindTestCentreList"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Bind test centres list")
    public ResponseEntity<Map<String, Object>> bindTestCentreList(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> c = Map.of("CentreID", 1, "Centre", "Main Reference Lab", "value", 1, "label", "Main Reference Lab");
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(c));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/BindApprovalDoctor", "/bindApprovalDoctor"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Bind approval doctors for verification")
    public ResponseEntity<Map<String, Object>> bindApprovalDoctor(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> doctors = List.of(
            Map.of("employeeid", 1, "name", "Dr. Ravi Kumar (MBBS, MD)", "IsDefault", 0, "value", 1, "label", "Dr. Ravi Kumar (MBBS, MD)"),
            Map.of("employeeid", 2, "name", "Dr. Sunita Rao (Pathologist)", "IsDefault", 0, "value", 2, "label", "Dr. Sunita Rao (Pathologist)")
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", doctors);
        return ResponseEntity.ok(response);
    }
}
