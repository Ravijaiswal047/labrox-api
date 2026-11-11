package com.labrox.iam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/Employee", "/api/v1/employee"})
@Tag(name = "Legacy Employee Details API Bridge")
public class LegacyEmployeeController {

    @PostMapping({"/getEmployeeDetailsByID", "/GetEmployeeDetailsByID"})
    @Operation(summary = "Get employee dashboard rights and font preferences")
    public ResponseEntity<Map<String, Object>> getEmployeeDetailsByID(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> empDetails = new HashMap<>();
        empDetails.put("EmployeeID", 1);
        empDetails.put("ShowDashboard", 1);
        empDetails.put("IsStoreDashboard", 0);
        empDetails.put("FontSize", 13);
        empDetails.put("applicationFont", "13");

        response.put("success", true);
        response.put("status", true);
        response.put("message", List.of(empDetails));

        return ResponseEntity.ok(response);
    }

    @GetMapping({"/ProEmployee", "/proEmployee"})
    @Operation(summary = "Get list of phlebotomists and PRO employees")
    public ResponseEntity<Map<String, Object>> getProEmployees() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> list = List.of(
            Map.of("EmployeeID", 1, "Name", "Dr. Ravi Sharma", "value", 1, "label", "Dr. Ravi Sharma"),
            Map.of("EmployeeID", 2, "Name", "Pooja Singh (Phlebo)", "value", 2, "label", "Pooja Singh (Phlebo)")
        );
        response.put("success", true);
        response.put("status", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/GetEmployeeDashboardAccess", "/getEmployeeDashboardAccess"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get employee dashboard access permissions")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboardAccess(@RequestParam(value = "EmployeeId", required = false) String employeeId) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> dashboards = List.of(
            Map.of("DashboardID", 1, "DashboardName", "Main Dashboard", "SequenceNo", 1, "IsActive", 1)
        );
        response.put("status", true);
        response.put("success", true);
        response.put("message", dashboards);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/WelcomeDetails", "/welcomeDetails"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get welcome banner and employee summary")
    public ResponseEntity<Map<String, Object>> getWelcomeDetails() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> details = new HashMap<>();
        details.put("EmployeeID", 1);
        details.put("Name", "Super Administrator");
        details.put("DesignationName", "Super Administrator");
        details.put("CentreName", "Main Reference Lab");
        response.put("status", true);
        response.put("success", true);
        response.put("message", List.of(details));
        return ResponseEntity.ok(response);
    }
}
