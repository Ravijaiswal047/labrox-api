package com.labrox.diagnostic.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DiagnosticSampleStore {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
    private static final DateTimeFormatter DT_ENTRY_FMT = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss");

    // Local override stores for test status and observation results
    private static final Map<Integer, Integer> TEST_STATUS_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, List<Map<String, Object>>> TEST_OBSERVATIONS_MAP = new ConcurrentHashMap<>();
    private static final List<Map<String, Object>> FALLBACK_SAMPLES = new ArrayList<>();

    static {
        resetLocalStore();
    }

    public static synchronized void resetLocalStore() {
        TEST_STATUS_MAP.clear();
        TEST_OBSERVATIONS_MAP.clear();
        FALLBACK_SAMPLES.clear();

        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DISPLAY_FMT);
        String dtEntry = now.format(DT_ENTRY_FMT);

        // Fallback Sample 1: Amit Sharma (WO-20261105-00001, Barcode BC20261105001, Status 3 = Received)
        Map<String, Object> s1 = createSample(
                201, 1, 2, "WO-20261105-00001", "LR2026-000001", "Amit Sharma", "30 Y", "Male",
                "Complete Blood Count (CBC)", "CBC01", 1, "Hematology", "HEM",
                "BC20261105001", "Whole Blood EDTA", 3, dateStr, dtEntry
        );
        FALLBACK_SAMPLES.add(s1);

        // Fallback Sample 2: Priya Patel (WO-20261105-00002, Barcode BC20261105002, Status 3 = Received)
        Map<String, Object> s2 = createSample(
                202, 2, 3, "WO-20261105-00002", "LR2026-000002", "Priya Patel", "28 Y", "Female",
                "Serum Creatinine", "BIO01", 2, "Biochemistry", "BIO",
                "BC20261105002", "Serum", 3, dateStr, dtEntry
        );
        FALLBACK_SAMPLES.add(s2);

        // Fallback Sample 3: Suresh Kumar (WO-20261105-00003, Barcode BC20261105003, Status 4 = Rejected)
        Map<String, Object> s3 = createSample(
                203, 3, 4, "WO-20261105-00003", "LR2026-000003", "Suresh Kumar", "52 Y", "Male",
                "Blood Sugar Fasting", "BIO02", 2, "Biochemistry", "BIO",
                "BC20261105003", "Fluoride Plasma", 4, dateStr, dtEntry
        );
        FALLBACK_SAMPLES.add(s3);

        // Fallback Sample 4: Rahul Verma (WO-20261105-00004, Barcode BC20261105004, Status 11 = Hold)
        Map<String, Object> s4 = createSample(
                204, 4, 5, "WO-20261105-00004", "LR2026-000004", "Rahul Verma", "45 Y", "Male",
                "Dengue Serology NS1", "MIC01", 4, "Microbiology", "MIC",
                "BC20261105004", "Serum", 11, dateStr, dtEntry
        );
        FALLBACK_SAMPLES.add(s4);

        // Fallback Sample 5: Pooja Verma (WO-20261105-00005, Barcode BC20261105005, Status 10 = Result Done)
        Map<String, Object> s5 = createSample(
                205, 5, 6, "WO-20261105-00005", "LR2026-000005", "Pooja Verma", "38 Y", "Female",
                "Lipid Profile", "BIO03", 2, "Biochemistry", "BIO",
                "BC20261105005", "Serum", 10, dateStr, dtEntry
        );
        FALLBACK_SAMPLES.add(s5);

        // Fallback Sample 6: Deepak Gupta (WO-20261105-00006, Barcode BC20261105006, Status 5 = Approved)
        Map<String, Object> s6 = createSample(
                206, 6, 7, "WO-20261105-00006", "LR2026-000006", "Deepak Gupta", "49 Y", "Male",
                "Thyroid Profile Total", "BIO04", 2, "Biochemistry", "BIO",
                "BC20261105006", "Serum", 5, dateStr, dtEntry
        );
        FALLBACK_SAMPLES.add(s6);
    }

    private static Map<String, Object> createSample(
            int testId, int investigationId, int ledgerId, String visitNo, String patientId,
            String pName, String age, String gender, String testName, String testCode,
            int deptId, String deptName, String deptCode, String barcode, String sampleType,
            int status, String dateStr, String dtEntry
    ) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("TestID", testId);
        item.put("InvestigationID", investigationId);
        item.put("LedgerTransactionID", ledgerId);
        item.put("VisitNo", visitNo);
        item.put("LedgerTransactionNo", visitNo);
        item.put("PatientID", patientId);
        item.put("PatientCode", patientId);
        item.put("UHID", patientId);
        item.put("PName", pName);
        item.put("PatientName", pName);
        item.put("Age", age);
        item.put("Gender", gender);
        item.put("Mobile", "9811122233");
        item.put("DoctorName", "Dr. Rajesh Sharma, MD");
        item.put("DoctorID", 2);
        item.put("DoctorReferal", "2");
        item.put("Department", deptName);
        item.put("DepartmentID", deptId);
        item.put("DepartmentCode", deptCode);
        item.put("ItemName", testName);
        item.put("Test", testName);
        item.put("TestName", testName);
        item.put("testName", testName);
        item.put("TestCode", testCode);
        item.put("BarcodeNo", barcode);
        item.put("SinNo", barcode);
        item.put("SINNo", barcode);
        item.put("BarcodeLogic", 1);
        item.put("BarcodeLogicID", 1);
        item.put("SampleTypeID", 1);
        item.put("SampleType", sampleType);
        item.put("SampleTypeName", sampleType);
        item.put("SampleName", sampleType);
        item.put("Status", status);
        item.put("IsSampleCollected", "Y");
        item.put("Date", dateStr);
        item.put("dtEntry", dtEntry);
        item.put("PreSampleCollectionDate", dateStr);
        item.put("CentreName", "Main Reference Lab");
        item.put("CentreID", 1);
        item.put("RateTypeName", "Standard Cash Rate");
        item.put("RateTypeCode", "CASH");
        item.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        item.put("TestIdhash", "hash" + testId);
        item.put("LedgertransactionIDHash", "hash_lt_" + ledgerId);
        item.put("TestCentreIDManual", 1);
        item.put("TestCentreID", 1);
        item.put("RemarksList", "[]");
        item.put("IssueRemarkList", "[]");
        item.put("IsApproved", status == 5 ? 1 : 0);
        item.put("Approved", status == 5 ? 1 : 0);
        item.put("IsPartialResult", 0);
        item.put("LabRemarkCount", 0);
        item.put("IssueCount", 0);
        item.put("isUrgent", 0);
        item.put("IsUrgent", 0);
        item.put("isOutSource", 0);
        item.put("IsVip", 0);
        item.put("HLMPatientType", "OPD");
        return item;
    }

    /**
     * Query samples across the clinical pipeline from sample-service or fallback
     */
    public static synchronized List<Map<String, Object>> getResultEntryWorklist(Map<String, Object> request) {
        List<Map<String, Object>> liveList = fetchFromSampleService();
        List<Map<String, Object>> rawList = new ArrayList<>();
        Set<Integer> existingTestIds = new HashSet<>();
        if (liveList != null) {
            for (Map<String, Object> s : liveList) {
                rawList.add(s);
                if (s.get("TestID") != null) {
                    try { existingTestIds.add(Integer.parseInt(s.get("TestID").toString())); } catch (Exception ignored) {}
                }
            }
        }
        for (Map<String, Object> fb : FALLBACK_SAMPLES) {
            if (fb.get("TestID") != null) {
                try {
                    int fbId = Integer.parseInt(fb.get("TestID").toString());
                    if (!existingTestIds.contains(fbId)) {
                        rawList.add(fb);
                    }
                } catch (Exception ignored) {}
            }
        }

        Object statusFilter = request != null ? request.get("SampleStatus") : null;
        Integer targetStatus = null;
        if (statusFilter != null && !statusFilter.toString().trim().isEmpty()
                && !statusFilter.toString().equalsIgnoreCase("All")
                && !statusFilter.toString().equals("0")) {
            try {
                targetStatus = Integer.parseInt(statusFilter.toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        String itemValue = request != null && request.get("ItemValue") != null ? request.get("ItemValue").toString().trim().toLowerCase() : "";
        String selectTypes = request != null && request.get("SelectTypes") != null ? request.get("SelectTypes").toString().trim() : "";

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> sample : rawList) {
            int testId = sample.get("TestID") != null ? Integer.parseInt(sample.get("TestID").toString()) : 0;
            // Apply local overridden status if modified (e.g. Tested -> 10, Approved -> 5)
            int currentStatus = TEST_STATUS_MAP.getOrDefault(testId, Integer.parseInt(sample.get("Status").toString()));

            // In Result Entry:
            // Status 16 in sample-service is mapped to Status 11 (Hold) in Result Entry
            if (currentStatus == 16) {
                currentStatus = 11;
            }

            // STRICT RESULT ENTRY ELIGIBILITY:
            // A sample MUST be Department Received (Status 3) or later (Tested=10, Approved=5, Hold=11, Printed=6, Dispatched=15)
            // Status 1 (Not Collected), Status 2 (Collected but NOT yet Department Received), and Status 4 (Rejected)
            // MUST NEVER appear in Result Entry under ANY circumstances!
            if (currentStatus <= 2 || currentStatus == 4) {
                continue;
            }

            // Filtering by status
            if (targetStatus != null && targetStatus != 0) {
                if (targetStatus == 11) {
                    if (currentStatus != 11 && currentStatus != 16) continue;
                } else if (currentStatus != targetStatus) {
                    continue;
                }
            }

            // Filter by search text if provided
            if (!itemValue.isEmpty()) {
                String barcode = sample.get("BarcodeNo") != null ? sample.get("BarcodeNo").toString().toLowerCase() : "";
                String sinNo = sample.get("SinNo") != null ? sample.get("SinNo").toString().toLowerCase() : "";
                String pid = sample.get("PatientID") != null ? sample.get("PatientID").toString().toLowerCase() : "";
                String pname = sample.get("PName") != null ? sample.get("PName").toString().toLowerCase() : "";
                String visit = sample.get("VisitNo") != null ? sample.get("VisitNo").toString().toLowerCase() : "";

                if ("BarcodeNo".equalsIgnoreCase(selectTypes)) {
                    if (!barcode.contains(itemValue) && !sinNo.contains(itemValue)) continue;
                } else if ("PatientCode".equalsIgnoreCase(selectTypes) || "PatientID".equalsIgnoreCase(selectTypes)) {
                    if (!pid.contains(itemValue)) continue;
                } else if ("PName".equalsIgnoreCase(selectTypes)) {
                    if (!pname.contains(itemValue)) continue;
                } else {
                    if (!barcode.contains(itemValue) && !pid.contains(itemValue) && !pname.contains(itemValue) && !visit.contains(itemValue)) continue;
                }
            }

            Map<String, Object> copy = new LinkedHashMap<>(sample);
            copy.put("Status", currentStatus);
            copy.put("IsApproved", currentStatus == 5 ? 1 : 0);
            copy.put("Approved", currentStatus == 5 ? 1 : 0);

            String resolvedTestName = sample.get("TestName") != null && !sample.get("TestName").toString().trim().isEmpty() ? sample.get("TestName").toString().trim()
                    : (sample.get("ItemName") != null && !sample.get("ItemName").toString().trim().isEmpty() ? sample.get("ItemName").toString().trim()
                    : (sample.get("Test") != null && !sample.get("Test").toString().trim().isEmpty() ? sample.get("Test").toString().trim() : "Complete Blood Count (CBC)"));
            copy.put("TestName", resolvedTestName);
            copy.put("testName", resolvedTestName);
            copy.put("ItemName", resolvedTestName);
            copy.put("Test", resolvedTestName);
            copy.put("PackageName", resolvedTestName);
            if (sample.get("DepartmentCode") == null) {
                String dept = sample.get("Department") != null ? sample.get("Department").toString().toUpperCase() : "HEM";
                copy.put("DepartmentCode", dept.length() >= 3 ? dept.substring(0, 3) : "HEM");
            }
            if (sample.get("TestCode") == null) {
                copy.put("TestCode", "T" + testId);
            }
            if (sample.get("RemarksList") == null) copy.put("RemarksList", "[]");
            if (sample.get("IssueRemarkList") == null) copy.put("IssueRemarkList", "[]");
            copy.put("IsPartialResult", 0);
            copy.put("LabRemarkCount", 0);
            copy.put("IssueCount", 0);
            copy.put("HLMPatientType", "OPD");
            copy.put("TestCentreIDManual", 1);
            copy.put("TestCentreID", 1);
            result.add(copy);
        }

        return result;
    }

    private static List<Map<String, Object>> fetchFromSampleService() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://localhost:8083/api/v1/DepartmentReceive/DepartmentReceive");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(3000);

            Map<String, Object> req = new HashMap<>();
            req.put("SampleStatus", "All");
            String json = OBJECT_MAPPER.writeValueAsString(req);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                Map<String, Object> resp = OBJECT_MAPPER.readValue(conn.getInputStream(), new TypeReference<>() {});
                if (resp.get("message") instanceof List<?>) {
                    return (List<Map<String, Object>>) resp.get("message");
                }
            }
        } catch (Exception e) {
            log.warn("Could not query sample-service DepartmentReceive: {}. Using fallback state.", e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    /**
     * Get observation parameters and test header for result entry
     */
    public static synchronized Map<String, Object> getResultEntryObservations(Map<String, Object> request) {
        log.info("DiagnosticSampleStore getResultEntryObservations called: {}", request);

        int testId = 201;
        if (request != null && request.get("TestID") != null) {
            String raw = request.get("TestID").toString().split(",")[0].trim();
            try {
                testId = Integer.parseInt(raw);
            } catch (NumberFormatException ignored) {}
        }

        int ledgerId = 2;
        if (request != null && request.get("LedgerTransactionID") != null) {
            try {
                ledgerId = Integer.parseInt(request.get("LedgerTransactionID").toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        String visitNo = request != null && request.get("VisitNo") != null ? request.get("VisitNo").toString() : "WO-20261105-00001";
        int currentStatus = TEST_STATUS_MAP.getOrDefault(testId, 3);

        // Dynamically resolve test header from live sample-service or fallback
        String testName = "Complete Blood Count (CBC)";
        String deptName = "Hematology";
        int deptId = 1;
        String sampleType = "Whole Blood EDTA";
        String barcode = "BC20261105001";
        int invId = 1;

        List<Map<String, Object>> liveList = fetchFromSampleService();
        if (liveList != null) {
            for (Map<String, Object> s : liveList) {
                if (s.get("TestID") != null && s.get("TestID").toString().equals(String.valueOf(testId))) {
                    if (s.get("Status") != null) {
                        try {
                            int sStatus = Integer.parseInt(s.get("Status").toString().trim());
                            currentStatus = TEST_STATUS_MAP.getOrDefault(testId, sStatus);
                        } catch (NumberFormatException ignored) {}
                    }
                    if (s.get("TestName") != null && !s.get("TestName").toString().trim().isEmpty()) testName = s.get("TestName").toString().trim();
                    else if (s.get("ItemName") != null && !s.get("ItemName").toString().trim().isEmpty()) testName = s.get("ItemName").toString().trim();
                    else if (s.get("Test") != null && !s.get("Test").toString().trim().isEmpty()) testName = s.get("Test").toString().trim();
                    if (s.get("Department") != null) deptName = s.get("Department").toString();
                    if (s.get("DepartmentID") != null) {
                        try { deptId = Integer.parseInt(s.get("DepartmentID").toString()); } catch (Exception ignored) {}
                    }
                    if (s.get("SampleTypeName") != null) sampleType = s.get("SampleTypeName").toString();
                    else if (s.get("SampleType") != null) sampleType = s.get("SampleType").toString();
                    if (s.get("BarcodeNo") != null) barcode = s.get("BarcodeNo").toString();
                    if (s.get("InvestigationID") != null) {
                        try { invId = Integer.parseInt(s.get("InvestigationID").toString()); } catch (Exception ignored) {}
                    }
                    if (s.get("VisitNo") != null) visitNo = s.get("VisitNo").toString();
                    break;
                }
            }
        }

        // STRICT ELIGIBILITY CHECK: Sample MUST be Department Received (Status >= 3, not rejected)
        if (currentStatus <= 2 || currentStatus == 4) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", false);
            response.put("success", false);
            response.put("message", "Sample has not been received in the department yet. Please perform Department Receive before entering results.");
            return response;
        }

        List<Map<String, Object>> observations = TEST_OBSERVATIONS_MAP.get(testId);
        if (observations == null || observations.isEmpty()) {
            observations = generateDefaultObservations(testId);
        }

        boolean isCbc = testName.toLowerCase().contains("cbc") || testName.toLowerCase().contains("complete blood");

        if (testId == 202 || testId == 102) {
            testName = "Serum Creatinine";
            deptName = "Biochemistry";
            deptId = 2;
            sampleType = "Serum";
            barcode = "BC20261105002";
            invId = 2;
            isCbc = false;
        } else if (testId == 203) {
            testName = "Blood Sugar Fasting";
            deptName = "Biochemistry";
            deptId = 2;
            sampleType = "Fluoride Plasma";
            barcode = "BC20261105003";
            invId = 3;
            isCbc = false;
        } else if (testId == 204) {
            testName = "Dengue Serology NS1";
            deptName = "Microbiology";
            deptId = 4;
            sampleType = "Serum";
            barcode = "BC20261105004";
            invId = 4;
            isCbc = false;
        } else if (testId == 205) {
            testName = "Lipid Profile";
            deptName = "Biochemistry";
            deptId = 2;
            sampleType = "Serum";
            barcode = "BC20261105005";
            invId = 5;
            isCbc = false;
        } else if (testId == 206) {
            testName = "Thyroid Profile Total";
            deptName = "Biochemistry";
            deptId = 2;
            sampleType = "Serum";
            barcode = "BC20261105006";
            invId = 6;
            isCbc = false;
        }

        List<Map<String, Object>> testHeaders = new ArrayList<>();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("InvestigationID", invId);
        header.put("TestName", testName);
        header.put("testName", testName);
        header.put("PackageName", testName);
        header.put("ItemName", testName);
        header.put("Test", testName);
        header.put("TestID", testId);
        header.put("LedgerTransactionID", ledgerId);
        header.put("VisitNo", visitNo);
        header.put("Department", deptName);
        header.put("DepartmentID", deptId);
        header.put("Status", currentStatus);
        header.put("Printwithhead", 1);
        header.put("IsDLCCheck", isCbc ? 1 : 0);
        header.put("IsSampleCollected", "Y");
        header.put("Approved", currentStatus == 5 ? 1 : 0);
        header.put("DefaultSignatureID", 1);
        header.put("SampleTypeName", sampleType);
        header.put("BarcodeNo", barcode);
        header.put("SinNo", barcode);
        header.put("OldValueDate", "");
        header.put("ResultFlag", 0);
        header.put("CommentIDDefault", "");
        header.put("Mobile", "9811122233");
        testHeaders.add(header);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", observations);
        payload.put("testHeader", testHeaders);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", payload);
        response.put("data", payload);
        return response;
    }

    private static List<Map<String, Object>> generateDefaultObservations(int testId) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (testId == 202) {
            // Serum Creatinine
            list.add(createObs(testId, 21, "Serum Creatinine", "", "0.7", "1.3", "0.3", "5.0", "mg/dL", "0.7 - 1.3", "Modified Jaffe", 1));
            list.add(createObs(testId, 22, "Blood Urea", "", "15.0", "45.0", "5.0", "100.0", "mg/dL", "15.0 - 45.0", "GLDH", 2));
            list.add(createObs(testId, 23, "BUN (Blood Urea Nitrogen)", "", "7.0", "20.0", "2.0", "50.0", "mg/dL", "7.0 - 20.0", "Calculated", 3));
        } else if (testId == 203) {
            // Blood Sugar Fasting
            list.add(createObs(testId, 31, "Fasting Blood Sugar", "", "70.0", "100.0", "40.0", "400.0", "mg/dL", "70.0 - 100.0", "GOD-POD", 1));
        } else if (testId == 204) {
            // Dengue Serology
            list.add(createObs(testId, 41, "Dengue NS1 Antigen", "", "", "", "", "", "", "Negative (Non-Reactive)", "Immunochromatography", 1));
        } else if (testId == 205) {
            // Lipid Profile
            list.add(createObs(testId, 51, "Total Cholesterol", "", "125.0", "200.0", "50.0", "400.0", "mg/dL", "< 200.0", "CHOD-PAP", 1));
            list.add(createObs(testId, 52, "Triglycerides", "", "50.0", "150.0", "20.0", "500.0", "mg/dL", "< 150.0", "GPO-PAP", 2));
            list.add(createObs(testId, 53, "HDL Cholesterol", "", "40.0", "60.0", "15.0", "100.0", "mg/dL", "40.0 - 60.0", "Direct Enzymatic", 3));
            list.add(createObs(testId, 54, "LDL Cholesterol", "", "60.0", "100.0", "20.0", "300.0", "mg/dL", "< 100.0", "Calculated", 4));
            list.add(createObs(testId, 55, "VLDL Cholesterol", "", "10.0", "30.0", "5.0", "100.0", "mg/dL", "10.0 - 30.0", "Calculated", 5));
        } else if (testId == 206) {
            // Thyroid Profile Total
            list.add(createObs(testId, 61, "Total Triiodothyronine (T3)", "", "0.80", "2.00", "0.20", "5.00", "ng/mL", "0.80 - 2.00", "CLIA", 1));
            list.add(createObs(testId, 62, "Total Thyroxine (T4)", "", "5.10", "14.10", "1.00", "25.00", "ug/dL", "5.10 - 14.10", "CLIA", 2));
            list.add(createObs(testId, 63, "Thyroid Stimulating Hormone (TSH)", "", "0.27", "4.20", "0.01", "50.00", "uIU/mL", "0.27 - 4.20", "CLIA", 3));
        } else {
            // CBC (Default for 201, 101, etc.)
            list.add(createObs(testId, 1, "Hemoglobin (Hb)", "", "13.0", "17.0", "7.0", "20.0", "g/dL", "13.0 - 17.0", "Spectrophotometry", 1));
            list.add(createObs(testId, 2, "Total Leukocyte Count (WBC)", "", "4000", "11000", "2000", "30000", "/cumm", "4000 - 11000", "Electrical Impedance", 2));
            list.add(createObs(testId, 3, "Platelet Count", "", "150000", "450000", "50000", "1000000", "/cumm", "150000 - 450000", "Electrical Impedance", 3));
            list.add(createObs(testId, 4, "Packed Cell Volume (PCV)", "", "40.0", "50.0", "20.0", "60.0", "%", "40.0 - 50.0", "Calculated", 4));
            list.add(createObs(testId, 5, "RBC Count", "", "4.5", "5.5", "2.0", "7.0", "mil/cumm", "4.5 - 5.5", "Electrical Impedance", 5));
            list.add(createObs(testId, 6, "Neutrophils", "", "40", "75", "15", "90", "%", "40 - 75", "Microscopy / VCS", 6));
            list.add(createObs(testId, 7, "Lymphocytes", "", "20", "45", "5", "70", "%", "20 - 45", "Microscopy / VCS", 7));
            list.add(createObs(testId, 8, "Eosinophils", "", "1", "6", "0", "20", "%", "1 - 6", "Microscopy / VCS", 8));
            list.add(createObs(testId, 9, "Monocytes", "", "2", "10", "0", "15", "%", "2 - 10", "Microscopy / VCS", 9));
            list.add(createObs(testId, 10, "Basophils", "", "0", "1", "0", "3", "%", "0 - 1", "Microscopy / VCS", 10));
        }
        return list;
    }

    private static Map<String, Object> createObs(
            int testId, int obsId, String name, String val, String min, String max,
            String minCrit, String maxCrit, String unit, String display, String method, int order
    ) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("TestID", testId);
        m.put("InvestigationID", 1);
        m.put("LabObservationID", obsId);
        m.put("LabObservationName", name);
        m.put("TestName", name);
        m.put("testName", name);
        m.put("ItemName", name);
        m.put("Test", name);
        m.put("PackageName", name);
        m.put("Value", val != null ? val : "");
        m.put("MinValue", min);
        m.put("MaxValue", max);
        m.put("MinCritical", minCrit);
        m.put("MaxCritical", maxCrit);
        m.put("ReadingFormat", unit);
        m.put("DisplayReading", display);
        m.put("Method", method);
        m.put("IsCritical", 0);
        m.put("MinFlag", 0);
        m.put("MaxFlag", 0);
        m.put("Status", TEST_STATUS_MAP.getOrDefault(testId, 3));
        m.put("PrintOrder", order);
        m.put("IsBold", 0);
        m.put("IsUnderline", 0);
        m.put("Formula", "");
        m.put("Header", 0);
        m.put("DefaultSignatureID", 1);
        m.put("TestCentreID", 1);
        m.put("COMMENT", "");
        m.put("HelpMenuBold", 0);
        m.put("TotalAgeInDays", 10950);
        return m;
    }

    public static synchronized void saveResultEntry(Object request) {
        log.info("DiagnosticSampleStore saveResultEntry called: {}", request);
        if (request instanceof Map<?, ?> map) {
            String resultStatus = map.get("ResultStatus") != null ? map.get("ResultStatus").toString().trim() : "Save";
            int newStatus = 10; // 10 = Result Done / Tested
            if ("Approved".equalsIgnoreCase(resultStatus)) {
                newStatus = 5; // 5 = Approved
            } else if ("Hold".equalsIgnoreCase(resultStatus)) {
                newStatus = 11; // 11 = Hold
            } else if ("Received".equalsIgnoreCase(resultStatus)) {
                newStatus = 3; // 3 = Received
            }

            Object headerInfoObj = map.get("HeaderInfo");
            if (headerInfoObj instanceof List<?> headers) {
                for (Object h : headers) {
                    if (h instanceof Map<?, ?> hMap && hMap.get("TestID") != null) {
                        try {
                            int tId = Integer.parseInt(hMap.get("TestID").toString().split(",")[0].trim());
                            TEST_STATUS_MAP.put(tId, newStatus);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            Object dataObj = map.get("data");
            if (dataObj instanceof List<?> rows && !rows.isEmpty()) {
                List<Map<String, Object>> savedObs = new ArrayList<>();
                int targetTestId = 0;
                for (Object r : rows) {
                    if (r instanceof Map<?, ?> rMap) {
                        Map<String, Object> obs = new LinkedHashMap<>();
                        for (Map.Entry<?, ?> e : rMap.entrySet()) {
                            obs.put(String.valueOf(e.getKey()), e.getValue());
                        }
                        if (rMap.get("TestID") != null) {
                            try {
                                targetTestId = Integer.parseInt(rMap.get("TestID").toString().split(",")[0].trim());
                            } catch (NumberFormatException ignored) {}
                        }
                        savedObs.add(obs);
                    }
                }
                if (targetTestId > 0) {
                    TEST_OBSERVATIONS_MAP.put(targetTestId, savedObs);
                    TEST_STATUS_MAP.put(targetTestId, newStatus);
                }
            }
        }
    }
}
