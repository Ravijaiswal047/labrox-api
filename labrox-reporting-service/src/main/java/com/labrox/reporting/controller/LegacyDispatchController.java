package com.labrox.reporting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping({"/api/v1/Dispatch", "/api/v1/dispatch"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Dispatch and Report Delivery API")
public class LegacyDispatchController {

    private static final List<Map<String, Object>> PIPELINE_ORDERS = new ArrayList<>();
    private static final Map<String, String> DOCKET_REGISTRY = new ConcurrentHashMap<>();

    static {
        seedClinicalPipelineData();
    }

    private static synchronized void seedClinicalPipelineData() {
        PIPELINE_ORDERS.clear();
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"));

        // 1. Amit Sharma - Status 5: Approved (CBC)
        Map<String, Object> r1 = new HashMap<>();
        r1.put("LedgerTransactionID", 1);
        r1.put("LedgerTransactionNo", "WO-20261105-00001");
        r1.put("VisitNo", "WO-20261105-00001");
        r1.put("PatientID", "LR2026-000001");
        r1.put("PatientCode", "LR2026-000001");
        r1.put("UHID", "LR2026-000001");
        r1.put("PName", "Amit Sharma");
        r1.put("Age", "30 Y");
        r1.put("Gender", "Male");
        r1.put("AgeGender", "30 Y / Male");
        r1.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r1.put("Centre", "Main Reference Lab");
        r1.put("CentreID", 1);
        r1.put("RateTypeName", "Standard Cash Rate");
        r1.put("GrossAmount", 500.0);
        r1.put("NetAmount", 500.0);
        r1.put("Adjustment", 500.0);
        r1.put("BalanceAmount", 0.0);
        r1.put("Status", 5); // Approved
        r1.put("Date", nowStr);
        r1.put("RegDate", nowStr);
        r1.put("IsSampleCollected", "Y");
        r1.put("IsApproved", 1);
        r1.put("SinNo", "BC20261105001");
        r1.put("BarcodeNo", "BC20261105001");
        r1.put("IsUrgent", 0);
        r1.put("IsVip", 0);
        r1.put("IsCourier", 0);
        r1.put("RemarksList", "[]");
        r1.put("LabRemarkCount", 0);
        r1.put("Test", "<input type=\"checkbox\" class=\"1\" value=\"hash101\" checked /> Complete Blood Count (CBC)");
        r1.put("PliDataList", "[{\"TestID\":101,\"TestName\":\"Complete Blood Count (CBC)\",\"SinNo\":\"BC20261105001\",\"BarcodeNo\":\"BC20261105001\",\"sampletypename\":\"Whole Blood EDTA\",\"IsApproved\":1,\"Status\":5,\"TestIdHash\":\"hash101\",\"TestIDHash\":\"hash101\",\"ResultRequiredName\":\"Complete Blood Count\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r1);

        // 2. Priya Patel - Status 5: Approved (Serum Creatinine)
        Map<String, Object> r2 = new HashMap<>();
        r2.put("LedgerTransactionID", 2);
        r2.put("LedgerTransactionNo", "WO-20261105-00002");
        r2.put("VisitNo", "WO-20261105-00002");
        r2.put("PatientID", "LR2026-000002");
        r2.put("PatientCode", "LR2026-000002");
        r2.put("UHID", "LR2026-000002");
        r2.put("PName", "Priya Patel");
        r2.put("Age", "28 Y");
        r2.put("Gender", "Female");
        r2.put("AgeGender", "28 Y / Female");
        r2.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r2.put("Centre", "Main Reference Lab");
        r2.put("CentreID", 1);
        r2.put("RateTypeName", "Standard Cash Rate");
        r2.put("GrossAmount", 350.0);
        r2.put("NetAmount", 350.0);
        r2.put("Adjustment", 350.0);
        r2.put("BalanceAmount", 0.0);
        r2.put("Status", 5); // Approved
        r2.put("Date", nowStr);
        r2.put("RegDate", nowStr);
        r2.put("IsSampleCollected", "Y");
        r2.put("IsApproved", 1);
        r2.put("SinNo", "BC20261105002");
        r2.put("BarcodeNo", "BC20261105002");
        r2.put("IsUrgent", 0);
        r2.put("IsVip", 0);
        r2.put("IsCourier", 0);
        r2.put("RemarksList", "[]");
        r2.put("LabRemarkCount", 0);
        r2.put("Test", "<input type=\"checkbox\" class=\"2\" value=\"hash202\" checked /> Serum Creatinine");
        r2.put("PliDataList", "[{\"TestID\":202,\"TestName\":\"Serum Creatinine\",\"SinNo\":\"BC20261105002\",\"BarcodeNo\":\"BC20261105002\",\"sampletypename\":\"Serum\",\"IsApproved\":1,\"Status\":5,\"TestIdHash\":\"hash202\",\"TestIDHash\":\"hash202\",\"ResultRequiredName\":\"Serum Creatinine\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r2);

        // 3. Walk-in Patient - Status 6: Printed (Lipid Profile)
        Map<String, Object> r3 = new HashMap<>();
        r3.put("LedgerTransactionID", 3);
        r3.put("LedgerTransactionNo", "WO-20261104-00109");
        r3.put("VisitNo", "WO-20261104-00109");
        r3.put("PatientID", "LR2026-000043");
        r3.put("PatientCode", "LR2026-000043");
        r3.put("UHID", "LR2026-000043");
        r3.put("PName", "Walk-in Patient");
        r3.put("Age", "35 Y");
        r3.put("Gender", "Male");
        r3.put("AgeGender", "35 Y / Male");
        r3.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r3.put("Centre", "Main Reference Lab");
        r3.put("CentreID", 1);
        r3.put("RateTypeName", "Standard Cash Rate");
        r3.put("GrossAmount", 750.0);
        r3.put("NetAmount", 750.0);
        r3.put("Adjustment", 750.0);
        r3.put("BalanceAmount", 0.0);
        r3.put("Status", 6); // Printed
        r3.put("Date", nowStr);
        r3.put("RegDate", nowStr);
        r3.put("IsSampleCollected", "Y");
        r3.put("IsApproved", 1);
        r3.put("SinNo", "BC20261104001");
        r3.put("BarcodeNo", "BC20261104001");
        r3.put("IsUrgent", 0);
        r3.put("IsVip", 0);
        r3.put("IsCourier", 0);
        r3.put("RemarksList", "[]");
        r3.put("LabRemarkCount", 0);
        r3.put("Test", "<input type=\"checkbox\" class=\"3\" value=\"hash102\" checked /> Lipid Profile");
        r3.put("PliDataList", "[{\"TestID\":102,\"TestName\":\"Lipid Profile\",\"SinNo\":\"BC20261104001\",\"BarcodeNo\":\"BC20261104001\",\"sampletypename\":\"Serum\",\"IsApproved\":1,\"Status\":6,\"TestIdHash\":\"hash102\",\"TestIDHash\":\"hash102\",\"ResultRequiredName\":\"Lipid Profile\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r3);

        // 4. Deepak Gupta - Status 15: Dispatched (Complete Blood Count)
        Map<String, Object> r4 = new HashMap<>();
        r4.put("LedgerTransactionID", 4);
        r4.put("LedgerTransactionNo", "WO-20261105-00005");
        r4.put("VisitNo", "WO-20261105-00005");
        r4.put("PatientID", "LR2026-000005");
        r4.put("PatientCode", "LR2026-000005");
        r4.put("UHID", "LR2026-000005");
        r4.put("PName", "Deepak Gupta");
        r4.put("Age", "38 Y");
        r4.put("Gender", "Male");
        r4.put("AgeGender", "38 Y / Male");
        r4.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r4.put("Centre", "Main Reference Lab");
        r4.put("CentreID", 1);
        r4.put("RateTypeName", "Standard Cash Rate");
        r4.put("GrossAmount", 500.0);
        r4.put("NetAmount", 500.0);
        r4.put("Adjustment", 500.0);
        r4.put("BalanceAmount", 0.0);
        r4.put("Status", 15); // Dispatched
        r4.put("Date", nowStr);
        r4.put("RegDate", nowStr);
        r4.put("IsSampleCollected", "Y");
        r4.put("IsApproved", 1);
        r4.put("SinNo", "BC20261105005");
        r4.put("BarcodeNo", "BC20261105005");
        r4.put("IsUrgent", 0);
        r4.put("IsVip", 0);
        r4.put("IsCourier", 1);
        r4.put("DocketNo", "DKT-BLR-90214");
        r4.put("RemarksList", "[]");
        r4.put("LabRemarkCount", 0);
        r4.put("Test", "<input type=\"checkbox\" class=\"4\" value=\"hash205\" checked /> Complete Blood Count (CBC)");
        r4.put("PliDataList", "[{\"TestID\":205,\"TestName\":\"Complete Blood Count (CBC)\",\"SinNo\":\"BC20261105005\",\"BarcodeNo\":\"BC20261105005\",\"sampletypename\":\"Whole Blood EDTA\",\"IsApproved\":1,\"Status\":15,\"TestIdHash\":\"hash205\",\"TestIDHash\":\"hash205\",\"ResultRequiredName\":\"Complete Blood Count\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r4);

        // 5. Pooja Verma - Status 10: Result Done (Thyroid Profile)
        Map<String, Object> r5 = new HashMap<>();
        r5.put("LedgerTransactionID", 5);
        r5.put("LedgerTransactionNo", "WO-20261105-00006");
        r5.put("VisitNo", "WO-20261105-00006");
        r5.put("PatientID", "LR2026-000006");
        r5.put("PatientCode", "LR2026-000006");
        r5.put("UHID", "LR2026-000006");
        r5.put("PName", "Pooja Verma");
        r5.put("Age", "32 Y");
        r5.put("Gender", "Female");
        r5.put("AgeGender", "32 Y / Female");
        r5.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r5.put("Centre", "Main Reference Lab");
        r5.put("CentreID", 1);
        r5.put("RateTypeName", "Standard Cash Rate");
        r5.put("GrossAmount", 600.0);
        r5.put("NetAmount", 600.0);
        r5.put("Adjustment", 600.0);
        r5.put("BalanceAmount", 0.0);
        r5.put("Status", 10); // Result Done
        r5.put("Date", nowStr);
        r5.put("RegDate", nowStr);
        r5.put("IsSampleCollected", "Y");
        r5.put("IsApproved", 0);
        r5.put("SinNo", "BC20261105006");
        r5.put("BarcodeNo", "BC20261105006");
        r5.put("IsUrgent", 0);
        r5.put("IsVip", 0);
        r5.put("IsCourier", 0);
        r5.put("RemarksList", "[]");
        r5.put("LabRemarkCount", 0);
        r5.put("Test", "<input type=\"checkbox\" class=\"5\" value=\"hash206\" checked /> Thyroid Profile (T3, T4, TSH)");
        r5.put("PliDataList", "[{\"TestID\":206,\"TestName\":\"Thyroid Profile (T3, T4, TSH)\",\"SinNo\":\"BC20261105006\",\"BarcodeNo\":\"BC20261105006\",\"sampletypename\":\"Serum\",\"IsApproved\":0,\"Status\":10,\"TestIdHash\":\"hash206\",\"TestIDHash\":\"hash206\",\"ResultRequiredName\":\"Thyroid Profile\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r5);

        // 6. Rahul Verma - Status 11: Hold (Dengue Serology NS1)
        Map<String, Object> r6 = new HashMap<>();
        r6.put("LedgerTransactionID", 6);
        r6.put("LedgerTransactionNo", "WO-20261105-00004");
        r6.put("VisitNo", "WO-20261105-00004");
        r6.put("PatientID", "LR2026-000004");
        r6.put("PatientCode", "LR2026-000004");
        r6.put("UHID", "LR2026-000004");
        r6.put("PName", "Rahul Verma");
        r6.put("Age", "45 Y");
        r6.put("Gender", "Male");
        r6.put("AgeGender", "45 Y / Male");
        r6.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r6.put("Centre", "Main Reference Lab");
        r6.put("CentreID", 1);
        r6.put("RateTypeName", "Standard Cash Rate");
        r6.put("GrossAmount", 800.0);
        r6.put("NetAmount", 800.0);
        r6.put("Adjustment", 800.0);
        r6.put("BalanceAmount", 0.0);
        r6.put("Status", 11); // Hold
        r6.put("Date", nowStr);
        r6.put("RegDate", nowStr);
        r6.put("IsSampleCollected", "Y");
        r6.put("IsApproved", 0);
        r6.put("SinNo", "BC20261105004");
        r6.put("BarcodeNo", "BC20261105004");
        r6.put("IsUrgent", 0);
        r6.put("IsVip", 0);
        r6.put("IsCourier", 0);
        r6.put("RemarksList", "[]");
        r6.put("LabRemarkCount", 0);
        r6.put("Test", "<input type=\"checkbox\" class=\"6\" value=\"hash204\" checked /> Dengue Serology NS1");
        r6.put("PliDataList", "[{\"TestID\":204,\"TestName\":\"Dengue Serology NS1\",\"SinNo\":\"BC20261105004\",\"BarcodeNo\":\"BC20261105004\",\"sampletypename\":\"Serum\",\"IsApproved\":0,\"Status\":11,\"TestIdHash\":\"hash204\",\"TestIDHash\":\"hash204\",\"ResultRequiredName\":\"Dengue Serology NS1\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r6);

        // 7. Suresh Kumar - Status 4: Rejected (Blood Sugar Fasting)
        Map<String, Object> r7 = new HashMap<>();
        r7.put("LedgerTransactionID", 7);
        r7.put("LedgerTransactionNo", "WO-20261105-00003");
        r7.put("VisitNo", "WO-20261105-00003");
        r7.put("PatientID", "LR2026-000003");
        r7.put("PatientCode", "LR2026-000003");
        r7.put("UHID", "LR2026-000003");
        r7.put("PName", "Suresh Kumar");
        r7.put("Age", "52 Y");
        r7.put("Gender", "Male");
        r7.put("AgeGender", "52 Y / Male");
        r7.put("DoctorName", "Dr. Rajesh Sharma, MD");
        r7.put("Centre", "Main Reference Lab");
        r7.put("CentreID", 1);
        r7.put("RateTypeName", "Standard Cash Rate");
        r7.put("GrossAmount", 150.0);
        r7.put("NetAmount", 150.0);
        r7.put("Adjustment", 150.0);
        r7.put("BalanceAmount", 0.0);
        r7.put("Status", 4); // Rejected
        r7.put("Date", nowStr);
        r7.put("RegDate", nowStr);
        r7.put("IsSampleCollected", "R");
        r7.put("IsApproved", 0);
        r7.put("SinNo", "BC20261105003");
        r7.put("BarcodeNo", "BC20261105003");
        r7.put("IsUrgent", 0);
        r7.put("IsVip", 0);
        r7.put("IsCourier", 0);
        r7.put("RemarksList", "[]");
        r7.put("LabRemarkCount", 0);
        r7.put("Test", "<input type=\"checkbox\" class=\"7\" value=\"hash203\" checked /> Blood Sugar Fasting");
        r7.put("PliDataList", "[{\"TestID\":203,\"TestName\":\"Blood Sugar Fasting\",\"SinNo\":\"BC20261105003\",\"BarcodeNo\":\"BC20261105003\",\"sampletypename\":\"Fluoride Plasma\",\"IsApproved\":0,\"Status\":4,\"TestIdHash\":\"hash203\",\"TestIDHash\":\"hash203\",\"ResultRequiredName\":\"Blood Sugar Fasting\",\"PackageName\":\"\",\"isUrgent\":0}]");
        PIPELINE_ORDERS.add(r7);
    }

    @PostMapping({"/PatientLabSearchPaging", "/patientLabSearchPaging",
                  "/PatientLabSearchPagingData", "/patientLabSearchPagingData"})
    @Operation(summary = "Search patient work orders for dispatch report delivery")
    public ResponseEntity<Map<String, Object>> patientLabSearchPaging(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Dispatch PatientLabSearchPaging called with: {}", request);

        Object sampleStatusObj = request != null ? request.get("SampleStatus") : null;
        String statusStr = (sampleStatusObj != null) ? sampleStatusObj.toString().trim() : "";

        String selectTypes = (request != null && request.get("SelectTypes") != null) ? request.get("SelectTypes").toString().trim() : "";
        String itemValue = (request != null && request.get("ItemValue") != null) ? request.get("ItemValue").toString().trim().toLowerCase() : "";

        List<Map<String, Object>> filtered = new ArrayList<>();

        synchronized (PIPELINE_ORDERS) {
            // Merge live approved orders if not already in PIPELINE_ORDERS
            List<Map<String, Object>> liveApproved = fetchLiveApprovedOrders();
            for (Map<String, Object> live : liveApproved) {
                String visit = String.valueOf(live.get("VisitNo"));
                boolean exists = PIPELINE_ORDERS.stream().anyMatch(po -> Objects.equals(po.get("VisitNo"), visit));
                if (!exists) {
                    Map<String, Object> newOrder = new LinkedHashMap<>(live);
                    newOrder.put("Status", 5);
                    newOrder.put("IsApproved", 1);
                    newOrder.put("PatientName", live.get("PName"));
                    newOrder.put("AgeGender", live.get("Age") + " / " + live.get("Gender"));
                    newOrder.put("Date", live.get("Date"));
                    newOrder.put("RegDate", live.get("Date"));
                    newOrder.put("SinNo", live.get("BarcodeNo"));
                    newOrder.put("GrossAmount", 500.0);
                    newOrder.put("NetAmount", 500.0);
                    newOrder.put("Adjustment", 500.0);
                    newOrder.put("BalanceAmount", 0.0);
                    newOrder.put("RemarksList", "[]");
                    newOrder.put("LabRemarkCount", 0);
                    newOrder.put("Test", "<input type=\"checkbox\" checked /> " + live.get("TestName"));
                    newOrder.put("PliDataList", "[{\"TestID\":" + live.get("TestID") + ",\"TestName\":\"" + live.get("TestName") + "\",\"SinNo\":\"" + live.get("BarcodeNo") + "\",\"BarcodeNo\":\"" + live.get("BarcodeNo") + "\",\"sampletypename\":\"" + live.get("SampleTypeName") + "\",\"IsApproved\":1,\"Status\":5,\"TestIdHash\":\"hash" + live.get("TestID") + "\"}]");
                    PIPELINE_ORDERS.add(newOrder);
                }
            }

            for (Map<String, Object> order : PIPELINE_ORDERS) {
                int status = Integer.parseInt(order.get("Status").toString());

                // 1. Filter by SampleStatus if provided
                if (!statusStr.isEmpty() && !statusStr.equalsIgnoreCase("All") && !statusStr.equals("0")) {
                    try {
                        int targetStatus = Integer.parseInt(statusStr);
                        if (status != targetStatus) {
                            continue;
                        }
                    } catch (NumberFormatException ignored) {}
                } else {
                    // Default view in Dispatch Report:
                    // Only show Approved (5), Printed (6), Dispatched (15), Result Done (10)
                    // STRICT DOWNSTREAM EXCLUSION:
                    // Status 1 (Not Collected), Status 2 (Collected), Status 3 (Received), Status 4 (Rejected)
                    // MUST NOT BE VISIBLE in default view!
                    if (status == 1 || status == 2 || status == 3 || status == 4) {
                        continue;
                    }
                }

                // 2. Filter by Search criteria if provided
                if (!itemValue.isEmpty()) {
                    String visitNo = order.get("VisitNo") != null ? order.get("VisitNo").toString().toLowerCase() : "";
                    String barcodeNo = order.get("BarcodeNo") != null ? order.get("BarcodeNo").toString().toLowerCase() : "";
                    String patientId = order.get("PatientID") != null ? order.get("PatientID").toString().toLowerCase() : "";
                    String pName = order.get("PName") != null ? order.get("PName").toString().toLowerCase() : "";

                    if ("VisitNo".equalsIgnoreCase(selectTypes) || "LabNo".equalsIgnoreCase(selectTypes)) {
                        if (!visitNo.contains(itemValue)) continue;
                    } else if ("BarcodeNo".equalsIgnoreCase(selectTypes)) {
                        if (!barcodeNo.contains(itemValue)) continue;
                    } else if ("PatientCode".equalsIgnoreCase(selectTypes) || "PatientID".equalsIgnoreCase(selectTypes) || "UHID".equalsIgnoreCase(selectTypes)) {
                        if (!patientId.contains(itemValue)) continue;
                    } else if ("PName".equalsIgnoreCase(selectTypes)) {
                        if (!pName.contains(itemValue)) continue;
                    } else {
                        // General fallback
                        if (!visitNo.contains(itemValue) && !barcodeNo.contains(itemValue) && !patientId.contains(itemValue) && !pName.contains(itemValue)) {
                            continue;
                        }
                    }
                }

                Map<String, Object> copy = new LinkedHashMap<>(order);
                copy.put("PatientName", copy.get("PName"));
                copy.put("Mobile", copy.getOrDefault("Mobile", "9876543210"));
                copy.put("PatientCode", copy.get("PatientID"));
                copy.put("UHID", copy.get("PatientID"));
                // Attach any saved docket info
                String visit = String.valueOf(copy.get("VisitNo"));
                if (DOCKET_REGISTRY.containsKey(visit)) {
                    copy.put("DocketNo", DOCKET_REGISTRY.get(visit));
                    copy.put("CourierNo", DOCKET_REGISTRY.get(visit));
                }

                filtered.add(copy);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("rows", filtered);
        data.put("testCount", filtered.size());
        data.put("totalRecords", filtered.size());

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("data", data);
        response.put("message", filtered);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SaveDocketNo", "/saveDocketNo"})
    @Operation(summary = "Save courier docket tracking number for report dispatch")
    public ResponseEntity<Map<String, Object>> saveDocketNo(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Dispatch SaveDocketNo called with: {}", request);
        if (request != null && request.get("CourierDetail") instanceof List<?>) {
            List<?> list = (List<?>) request.get("CourierDetail");
            for (Object item : list) {
                if (item instanceof Map<?, ?>) {
                    Map<?, ?> m = (Map<?, ?>) item;
                    String visit = String.valueOf(m.get("VisitNo") != null ? m.get("VisitNo") : m.get("LedgerTransactionNo"));
                    String docket = String.valueOf(m.get("DocketNo") != null ? m.get("DocketNo") : m.get("CourierNo"));
                    if (!visit.isEmpty() && !docket.isEmpty()) {
                        DOCKET_REGISTRY.put(visit, docket);
                    }
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Docket details saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/UpdateDispatchStatus", "/updateDispatchStatus", "/Delivery", "/delivery"})
    @Operation(summary = "Mark report work order as dispatched or delivered")
    public ResponseEntity<Map<String, Object>> updateDispatchStatus(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Dispatch UpdateDispatchStatus called with: {}", request);
        String visitNo = (request != null && request.get("VisitNo") != null) ? request.get("VisitNo").toString() : "";

        synchronized (PIPELINE_ORDERS) {
            for (Map<String, Object> order : PIPELINE_ORDERS) {
                if (visitNo.isEmpty() || visitNo.equalsIgnoreCase(String.valueOf(order.get("VisitNo")))) {
                    order.put("Status", 15); // Dispatched
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Report status updated to Dispatched successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/DynamicLabSearch", "/dynamicLabSearch"})
    @Operation(summary = "Dynamic patient lab work order search")
    public ResponseEntity<Map<String, Object>> dynamicLabSearch(@RequestBody(required = false) Map<String, Object> request) {
        log.info("Dispatch DynamicLabSearch called with: {}", request);
        return patientLabSearchPaging(request);
    }

    private static List<Map<String, Object>> fetchLiveApprovedOrders() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://localhost:8084/api/v1/RE/GetResultEntry");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(3000);

            Map<String, Object> req = new HashMap<>();
            req.put("SampleStatus", "5"); // Approved
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(req);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                Map<String, Object> resp = mapper.readValue(conn.getInputStream(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
                if (resp.get("message") instanceof List<?> list) {
                    List<Map<String, Object>> res = new ArrayList<>();
                    for (Object obj : list) {
                        if (obj instanceof Map<?, ?> m) {
                            Map<String, Object> row = new LinkedHashMap<>();
                            for (Map.Entry<?, ?> e : m.entrySet()) {
                                row.put(String.valueOf(e.getKey()), e.getValue());
                            }
                            res.add(row);
                        }
                    }
                    return res;
                }
            }
        } catch (Exception e) {
            log.debug("Could not query RE GetResultEntry for approved orders: {}", e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
        return Collections.emptyList();
    }
}
