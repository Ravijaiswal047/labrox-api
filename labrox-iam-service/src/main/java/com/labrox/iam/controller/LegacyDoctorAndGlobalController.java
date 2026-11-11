package com.labrox.iam.controller;

import com.labrox.iam.service.GlobalSeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Legacy Doctor, Global Data & Test Catalog API Bridge")
public class LegacyDoctorAndGlobalController {

    private final GlobalSeedService globalSeedService;

    public LegacyDoctorAndGlobalController(GlobalSeedService globalSeedService) {
        this.globalSeedService = globalSeedService;
    }

    @PostMapping({"/api/v1/DoctorReferal/getDoctorData", "/api/v1/doctorReferal/getDoctorData",
                  "/api/v1/DoctorReferal/getDoctorDataBind", "/api/v1/doctorReferal/getDoctorDataBind",
                  "/api/v1/DoctorReferal/getsecondDoctorSuggestion", "/api/v1/doctorReferal/getsecondDoctorSuggestion"})
    @Operation(summary = "Search referral doctors by name or code")
    public ResponseEntity<Map<String, Object>> getDoctorData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("message", globalSeedService.getDoctors());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/Global/getGlobalData", "/api/v1/global/getGlobalData",
                             "/api/v1/Global/GetGlobalData", "/api/v1/global/GetGlobalData",
                             "/api/v1/Global/getGlobaldata", "/api/v1/global/getGlobaldata",
                             "/api/v1/Global/GetGlobaldata", "/api/v1/global/GetGlobaldata"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Get global lookups like PaymentMode, Gender, Title")
    public ResponseEntity<Map<String, Object>> getGlobalData(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(value = "Type", required = false) String paramType) {
        Map<String, Object> response = new HashMap<>();
        String type = "PaymentMode";
        if (body != null && body.get("Type") != null) {
            type = body.get("Type").toString();
        } else if (paramType != null && !paramType.trim().isEmpty()) {
            type = paramType.trim();
        }

        List<Map<String, Object>> list = globalSeedService.getGlobalData(type);

        response.put("success", true);
        response.put("status", true);
        response.put("message", list);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/DiscApproval/BindDiscApproval", "/api/v1/discApproval/bindDiscApproval"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Bind discount approval employees")
    public ResponseEntity<Map<String, Object>> bindDiscApproval() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("message", globalSeedService.getDiscApprovalEmployees());
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> buildTestItem(int id, String name, String code, double rate, int sampleTypeId, String sampleTypeName, String colorCode, int deptId, String deptName) {
        Map<String, Object> t = new HashMap<>();
        t.put("InvestigationID", id);
        t.put("TestID", id);
        t.put("TestName", name);
        t.put("TestCode", code);
        t.put("Rate", rate);
        t.put("Amount", rate);
        t.put("DataType", "Investigation");
        t.put("SampleTypeID", sampleTypeId);
        t.put("SampleTypeName", sampleTypeName);
        t.put("ColorCode", colorCode);
        t.put("Gender", "Both");
        t.put("FromAge", 0);
        t.put("ToAge", 36500);
        t.put("IsSampleRequired", "Sample Required");
        t.put("DepartmentID", deptId);
        t.put("DepartmentName", deptName);
        t.put("IsAllowTest", 1);
        t.put("todaybookcount", 0);
        return t;
    }

    @PostMapping({"/api/v1/TestData/BindBillingTestData", "/api/v1/testData/bindBillingTestData",
                  "/api/v1/TestData/BindBillingAllTestDataInfo", "/api/v1/testData/bindBillingAllTestDataInfo"})
    @Operation(summary = "Search investigations and packages for billing order entry")
    public ResponseEntity<Map<String, Object>> bindBillingTestData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> tests = List.of(
            buildTestItem(1, "Complete Blood Count (CBC)", "CBC001", 500.0, 1, "EDTA Whole Blood", "#800080", 1, "Hematology"),
            buildTestItem(2, "Lipid Profile (Cholesterol, Triglycerides, HDL, LDL)", "LIP001", 1000.0, 2, "Serum", "#FFD700", 2, "Biochemistry"),
            buildTestItem(3, "Liver Function Test (LFT)", "LFT001", 850.0, 2, "Serum", "#FFD700", 2, "Biochemistry"),
            buildTestItem(4, "Kidney Function Test (KFT / RFT)", "KFT001", 800.0, 2, "Serum", "#FFD700", 2, "Biochemistry"),
            buildTestItem(5, "Thyroid Profile Total (T3, T4, TSH)", "THY001", 750.0, 2, "Serum", "#FFD700", 2, "Biochemistry"),
            buildTestItem(6, "HbA1c - Glycated Hemoglobin", "HBA001", 600.0, 1, "EDTA Whole Blood", "#800080", 1, "Hematology"),
            buildTestItem(7, "Fasting Blood Glucose", "GLU001", 150.0, 3, "Fluoride Plasma", "#A9A9A9", 2, "Biochemistry")
        );

        response.put("success", true);
        response.put("status", true);
        response.put("message", tests);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/TestData/BindPackage", "/api/v1/testData/bindPackage"})
    public ResponseEntity<Map<String, Object>> bindPackage(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/TestData/BindSingleTestData", "/api/v1/testData/bindSingleTestData"})
    @Operation(summary = "Fetch specific investigation details, specimen tube, and rate")
    public ResponseEntity<Map<String, Object>> bindSingleTestData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        int invId = 1;
        if (body != null && body.get("InvestigationID") != null) {
            try {
                invId = Integer.parseInt(body.get("InvestigationID").toString());
            } catch (Exception ignored) {}
        }

        Map<String, Object> test = new HashMap<>();
        test.put("InvestigationID", invId);
        test.put("TestID", invId);
        test.put("testID", invId);
        test.put("testId", invId);
        test.put("ItemId", invId);

        String testName = "Complete Blood Count (CBC)";
        String testCode = "CBC001";
        double rate = 500.0;
        int sampleTypeId = 1;
        String sampleTypeName = "EDTA Whole Blood";
        String colorCode = "#800080";
        String container = "Lavender Top EDTA";
        int deptId = 1;
        String deptName = "Hematology";

        if (invId == 2) {
            testName = "Lipid Profile (Cholesterol, Triglycerides, HDL, LDL)";
            testCode = "LIP001";
            rate = 1000.0;
            sampleTypeId = 2;
            sampleTypeName = "Serum";
            colorCode = "#FFD700";
            container = "Gold Top SST";
            deptId = 2;
            deptName = "Biochemistry";
        } else if (invId == 3) {
            testName = "Liver Function Test (LFT)";
            testCode = "LFT001";
            rate = 850.0;
            sampleTypeId = 2;
            sampleTypeName = "Serum";
            colorCode = "#FFD700";
            container = "Gold Top SST";
            deptId = 2;
            deptName = "Biochemistry";
        } else if (invId == 4) {
            testName = "Kidney Function Test (KFT / RFT)";
            testCode = "KFT001";
            rate = 800.0;
            sampleTypeId = 2;
            sampleTypeName = "Serum";
            colorCode = "#FFD700";
            container = "Gold Top SST";
            deptId = 2;
            deptName = "Biochemistry";
        } else if (invId == 5) {
            testName = "Thyroid Profile Total (T3, T4, TSH)";
            testCode = "THY001";
            rate = 750.0;
            sampleTypeId = 2;
            sampleTypeName = "Serum";
            colorCode = "#FFD700";
            container = "Gold Top SST";
            deptId = 2;
            deptName = "Biochemistry";
        } else if (invId == 6) {
            testName = "HbA1c - Glycated Hemoglobin";
            testCode = "HBA001";
            rate = 600.0;
            sampleTypeId = 1;
            sampleTypeName = "EDTA Whole Blood";
            colorCode = "#800080";
            container = "Lavender Top EDTA";
            deptId = 1;
            deptName = "Hematology";
        } else if (invId == 7) {
            testName = "Fasting Blood Glucose";
            testCode = "GLU001";
            rate = 150.0;
            sampleTypeId = 3;
            sampleTypeName = "Fluoride Plasma";
            colorCode = "#A9A9A9";
            container = "Grey Top Fluoride";
            deptId = 2;
            deptName = "Biochemistry";
        }

        test.put("TestName", testName);
        test.put("TestCode", testCode);
        test.put("Rate", rate);
        test.put("Amount", rate);
        test.put("Discount", 0.0);
        test.put("DiscountAmt", 0.0);
        test.put("DiscAmt", 0.0);
        test.put("Gender", "Both");
        test.put("FromAge", 0);
        test.put("ToAge", 36500);
        test.put("IsSampleRequired", "Sample Required");
        test.put("SampleTypeID", sampleTypeId);
        test.put("SampleTypeName", sampleTypeName);
        test.put("ColorCode", colorCode);
        test.put("SampleContainer", container);
        test.put("DepartmentID", deptId);
        test.put("DepartmentName", deptName);
        test.put("IsAllowTest", 1);
        test.put("todaybookcount", 0);
        test.put("Urgent", 0);
        test.put("UrgentDeliveryDate", "2026-11-06 18:00:00");
        test.put("deliveryDate", "2026-11-06 18:00:00");
        test.put("isOutSource", 0);
        test.put("isLabOutSource", 0);
        test.put("IsCulture", 0);
        test.put("IsConcern", 0);
        test.put("IsPndtForm", 0);
        test.put("Radiology", 0);
        test.put("IsMarkerTest", 0);
        test.put("paybypanel", "0");
        test.put("paybypatient", "0");

        Map<String, Object> sampleTypeItem = new HashMap<>();
        sampleTypeItem.put("SampleTypeID", sampleTypeId);
        sampleTypeItem.put("SampleTypeName", sampleTypeName);
        sampleTypeItem.put("ColorCode", colorCode);
        sampleTypeItem.put("Container", container);
        sampleTypeItem.put("SampleQty", "2 ml");
        sampleTypeItem.put("Barcode", "");
        test.put("InvestigationSampleType", List.of(sampleTypeItem));

        response.put("success", true);
        response.put("status", true);
        response.put("message", List.of(test));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"/api/v1/Investigations/EditInvestigation", "/api/v1/investigations/editInvestigation"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> editInvestigation() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("observationData", Collections.emptyList());
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }
}
