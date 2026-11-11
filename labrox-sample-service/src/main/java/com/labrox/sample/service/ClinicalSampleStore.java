package com.labrox.sample.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ClinicalSampleStore {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DT_ENTRY_FMT = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    // Primary clinical data stores
    public static final Map<Integer, Map<String, Object>> WORK_ORDERS = new ConcurrentHashMap<>();
    public static final Map<Integer, List<Map<String, Object>>> INVESTIGATIONS = new ConcurrentHashMap<>();
    public static final Set<String> SRA_SORTED_BARCODES = Collections.synchronizedSet(new HashSet<>());
    public static final List<Map<String, Object>> AUDIT_LOG = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger LEDGER_SEQ = new AtomicInteger(10);
    private static final AtomicInteger TEST_SEQ = new AtomicInteger(500);

    public static synchronized void recordAudit(String action, String barcode, String details, String user, boolean success) {
        Map<String, Object> entry = new LinkedHashMap<>();
        String now = LocalDateTime.now().format(DT_ENTRY_FMT);
        entry.put("timestamp", now);
        entry.put("action", action);
        entry.put("barcode", barcode != null ? barcode : "");
        entry.put("details", details != null ? details : "");
        entry.put("user", user != null ? user : "SYSTEM");
        entry.put("success", success);
        AUDIT_LOG.add(entry);
        if (success) {
            log.info("[AUDIT 21-CFR-11] Action: {}, Barcode: {}, User: {}, Details: {}", action, barcode, user, details);
        } else {
            log.warn("[AUDIT VIOLATION 21-CFR-11] Action: {}, Barcode: {}, User: {}, Details: {}", action, barcode, user, details);
        }
    }

    static {
        resetInitialData();
    }

    public static synchronized void resetInitialData() {
        WORK_ORDERS.clear();
        INVESTIGATIONS.clear();
        SRA_SORTED_BARCODES.clear();
        AUDIT_LOG.clear();

        LocalDateTime now = LocalDateTime.now();
        String dtEntry = now.format(DT_ENTRY_FMT);
        String dateStr = now.format(DISPLAY_FMT);

        // -------------------------------------------------------------
        // Work Order 1: WO-20261104-00109 (Walk-in Patient) -> Status 1 (Pending Collection)
        // -------------------------------------------------------------
        Map<String, Object> wo1 = new LinkedHashMap<>();
        wo1.put("LedgerTransactionID", 1);
        wo1.put("VisitNo", "WO-20261104-00109");
        wo1.put("LedgerTransactionNo", "WO-20261104-00109");
        wo1.put("PName", "Walk-in Patient");
        wo1.put("PatientName", "Walk-in Patient");
        wo1.put("PatientID", "LR2026-000043");
        wo1.put("PatientCode", "LR2026-000043");
        wo1.put("UHID", "LR2026-000043");
        wo1.put("Age", "35 Y");
        wo1.put("Gender", "Male");
        wo1.put("Mobile", "9876543210");
        wo1.put("DoctorName", "Dr. Rajesh Sharma, MD");
        wo1.put("DoctorID", 2);
        wo1.put("DoctorReferal", "2");
        wo1.put("Date", dateStr);
        wo1.put("dtEntry", dtEntry);
        wo1.put("PreSampleCollectionDate", dateStr);
        wo1.put("Status", 1); // 1 = Not Collected
        wo1.put("IsSampleCollected", "N");
        wo1.put("is_sra", 0);
        wo1.put("sra_status", "PENDING");
        wo1.put("sra_category", "");
        wo1.put("sra_operator", "");
        wo1.put("sra_completed_at", "");
        wo1.put("SinNo", "");
        wo1.put("SINNo", "");
        wo1.put("BarcodeNo", "");
        wo1.put("BarcodeLogic", 1);
        wo1.put("BarcodeLogicID", 1);
        wo1.put("Department", "Hematology");
        wo1.put("DepartmentID", 1);
        wo1.put("InvestigationID", 1);
        wo1.put("Test", "Complete Blood Count (CBC)");
        wo1.put("TestName", "Complete Blood Count (CBC)");
        wo1.put("ItemName", "Complete Blood Count (CBC)");
        wo1.put("RateTypeName", "Standard Cash Rate");
        wo1.put("RateTypeCode", "CASH");
        wo1.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        wo1.put("CentreName", "Main Reference Lab");
        wo1.put("CentreID", 1);

        List<Map<String, Object>> items1 = new ArrayList<>();
        Map<String, Object> item1_1 = createTestItem(
                101, 1, 1, "WO-20261104-00109", "LR2026-000043", "Walk-in Patient", "35 Y", "Male",
                1, "Complete Blood Count (CBC)", 1, "Hematology", 1, "Whole Blood EDTA",
                "", 1, "N", dateStr, dtEntry, "#800080"
        );
        items1.add(item1_1);

        Map<String, Object> item1_2 = createTestItem(
                102, 1, 1, "WO-20261104-00109", "LR2026-000043", "Walk-in Patient", "35 Y", "Male",
                2, "Lipid Profile", 2, "Biochemistry", 2, "Serum",
                "", 1, "N", dateStr, dtEntry, "#FFD700"
        );
        items1.add(item1_2);

        INVESTIGATIONS.put(1, items1);
        updatePliDataList(wo1, items1);
        WORK_ORDERS.put(1, wo1);

        // -------------------------------------------------------------
        // Work Order 2: WO-20261105-00001 (Amit Sharma) -> Status 2 (Collected, Pending Dept Receive)
        // -------------------------------------------------------------
        Map<String, Object> wo2 = new LinkedHashMap<>();
        wo2.put("LedgerTransactionID", 2);
        wo2.put("VisitNo", "WO-20261105-00001");
        wo2.put("LedgerTransactionNo", "WO-20261105-00001");
        wo2.put("PName", "Amit Sharma");
        wo2.put("PatientName", "Amit Sharma");
        wo2.put("PatientID", "LR2026-000001");
        wo2.put("PatientCode", "LR2026-000001");
        wo2.put("UHID", "LR2026-000001");
        wo2.put("Age", "30 Y");
        wo2.put("Gender", "Male");
        wo2.put("Mobile", "9811122233");
        wo2.put("DoctorName", "Dr. Rajesh Sharma, MD");
        wo2.put("DoctorID", 2);
        wo2.put("DoctorReferal", "2");
        wo2.put("Date", dateStr);
        wo2.put("dtEntry", dtEntry);
        wo2.put("PreSampleCollectionDate", dateStr);
        wo2.put("Status", 2); // 2 = Collected
        wo2.put("IsSampleCollected", "Y");
        wo2.put("is_sra", 0);
        wo2.put("sra_status", "PENDING");
        wo2.put("sra_category", "");
        wo2.put("sra_operator", "");
        wo2.put("sra_completed_at", "");
        wo2.put("SinNo", "BC20261105001");
        wo2.put("SINNo", "BC20261105001");
        wo2.put("BarcodeNo", "BC20261105001");
        wo2.put("BarcodeLogic", 1);
        wo2.put("BarcodeLogicID", 1);
        wo2.put("Department", "Hematology");
        wo2.put("DepartmentID", 1);
        wo2.put("InvestigationID", 1);
        wo2.put("Test", "Complete Blood Count (CBC)");
        wo2.put("TestName", "Complete Blood Count (CBC)");
        wo2.put("ItemName", "Complete Blood Count (CBC)");
        wo2.put("RateTypeName", "Standard Cash Rate");
        wo2.put("RateTypeCode", "CASH");
        wo2.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        wo2.put("CentreName", "Main Reference Lab");
        wo2.put("CentreID", 1);

        List<Map<String, Object>> items2 = new ArrayList<>();
        Map<String, Object> item2_1 = createTestItem(
                201, 2, 2, "WO-20261105-00001", "LR2026-000001", "Amit Sharma", "30 Y", "Male",
                1, "Complete Blood Count (CBC)", 1, "Hematology", 1, "Whole Blood EDTA",
                "BC20261105001", 2, "Y", dateStr, dtEntry, "#ffa500"
        );
        items2.add(item2_1);

        INVESTIGATIONS.put(2, items2);
        updatePliDataList(wo2, items2);
        WORK_ORDERS.put(2, wo2);

        // -------------------------------------------------------------
        // Work Order 3: WO-20261105-00002 (Priya Patel) -> Status 3 (Department Received)
        // -------------------------------------------------------------
        Map<String, Object> wo3 = new LinkedHashMap<>();
        wo3.put("LedgerTransactionID", 3);
        wo3.put("VisitNo", "WO-20261105-00002");
        wo3.put("LedgerTransactionNo", "WO-20261105-00002");
        wo3.put("PName", "Priya Patel");
        wo3.put("PatientName", "Priya Patel");
        wo3.put("PatientID", "LR2026-000002");
        wo3.put("PatientCode", "LR2026-000002");
        wo3.put("UHID", "LR2026-000002");
        wo3.put("Age", "28 Y");
        wo3.put("Gender", "Female");
        wo3.put("Mobile", "9822233344");
        wo3.put("DoctorName", "Dr. Rajesh Sharma, MD");
        wo3.put("DoctorID", 2);
        wo3.put("DoctorReferal", "2");
        wo3.put("Date", dateStr);
        wo3.put("dtEntry", dtEntry);
        wo3.put("PreSampleCollectionDate", dateStr);
        wo3.put("Status", 3); // 3 = Department Received
        wo3.put("IsSampleCollected", "Y");
        wo3.put("is_sra", 1);
        wo3.put("sra_status", "SRA_COMPLETED");
        wo3.put("sra_category", "In-House");
        wo3.put("sra_operator", "Lab Tech (SRA)");
        wo3.put("sra_completed_at", dateStr);
        SRA_SORTED_BARCODES.add("BC20261105002");
        wo3.put("SinNo", "BC20261105002");
        wo3.put("SINNo", "BC20261105002");
        wo3.put("BarcodeNo", "BC20261105002");
        wo3.put("BarcodeLogic", 1);
        wo3.put("BarcodeLogicID", 1);
        wo3.put("Department", "Biochemistry");
        wo3.put("DepartmentID", 2);
        wo3.put("InvestigationID", 2);
        wo3.put("Test", "Serum Creatinine");
        wo3.put("TestName", "Serum Creatinine");
        wo3.put("ItemName", "Serum Creatinine");
        wo3.put("RateTypeName", "Standard Cash Rate");
        wo3.put("RateTypeCode", "CASH");
        wo3.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        wo3.put("CentreName", "Main Reference Lab");
        wo3.put("CentreID", 1);

        List<Map<String, Object>> items3 = new ArrayList<>();
        Map<String, Object> item3_1 = createTestItem(
                202, 3, 3, "WO-20261105-00002", "LR2026-000002", "Priya Patel", "28 Y", "Female",
                2, "Serum Creatinine", 2, "Biochemistry", 2, "Serum",
                "BC20261105002", 3, "Y", dateStr, dtEntry, "#008000"
        );
        item3_1.put("is_sra", 1);
        item3_1.put("sra_status", "SRA_COMPLETED");
        item3_1.put("sra_category", "In-House");
        item3_1.put("sra_operator", "Lab Tech (SRA)");
        item3_1.put("sra_completed_at", dateStr);
        items3.add(item3_1);

        INVESTIGATIONS.put(3, items3);
        updatePliDataList(wo3, items3);
        WORK_ORDERS.put(3, wo3);

        // -------------------------------------------------------------
        // Work Order 4: WO-20261105-00003 (Suresh Kumar) -> Status 4 (Sample Rejected)
        // -------------------------------------------------------------
        Map<String, Object> wo4 = new LinkedHashMap<>();
        wo4.put("LedgerTransactionID", 4);
        wo4.put("VisitNo", "WO-20261105-00003");
        wo4.put("LedgerTransactionNo", "WO-20261105-00003");
        wo4.put("PName", "Suresh Kumar");
        wo4.put("PatientName", "Suresh Kumar");
        wo4.put("PatientID", "LR2026-000003");
        wo4.put("PatientCode", "LR2026-000003");
        wo4.put("UHID", "LR2026-000003");
        wo4.put("Age", "52 Y");
        wo4.put("Gender", "Male");
        wo4.put("Mobile", "9833344455");
        wo4.put("DoctorName", "Dr. Rajesh Sharma, MD");
        wo4.put("DoctorID", 2);
        wo4.put("DoctorReferal", "2");
        wo4.put("Date", dateStr);
        wo4.put("dtEntry", dtEntry);
        wo4.put("PreSampleCollectionDate", dateStr);
        wo4.put("Status", 4); // 4 = Sample Rejected
        wo4.put("IsSampleCollected", "R");
        wo4.put("SinNo", "BC20261105003");
        wo4.put("SINNo", "BC20261105003");
        wo4.put("BarcodeNo", "BC20261105003");
        wo4.put("BarcodeLogic", 1);
        wo4.put("BarcodeLogicID", 1);
        wo4.put("Department", "Biochemistry");
        wo4.put("DepartmentID", 2);
        wo4.put("InvestigationID", 3);
        wo4.put("Test", "Blood Sugar Fasting");
        wo4.put("TestName", "Blood Sugar Fasting");
        wo4.put("ItemName", "Blood Sugar Fasting");
        wo4.put("RateTypeName", "Standard Cash Rate");
        wo4.put("RateTypeCode", "CASH");
        wo4.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        wo4.put("CentreName", "Main Reference Lab");
        wo4.put("CentreID", 1);

        List<Map<String, Object>> items4 = new ArrayList<>();
        Map<String, Object> item4_1 = createTestItem(
                203, 4, 4, "WO-20261105-00003", "LR2026-000003", "Suresh Kumar", "52 Y", "Male",
                3, "Blood Sugar Fasting", 2, "Biochemistry", 3, "Fluoride Plasma",
                "BC20261105003", 4, "R", dateStr, dtEntry, "#B0C4DE"
        );
        item4_1.put("SampleRemarks", "Hemolysed Specimen");
        item4_1.put("isReject", 1);
        item4_1.put("isSampleReCollection", 1);
        items4.add(item4_1);

        INVESTIGATIONS.put(4, items4);
        updatePliDataList(wo4, items4);
        WORK_ORDERS.put(4, wo4);

        // -------------------------------------------------------------
        // Work Order 5: WO-20261105-00004 (Rahul Verma) -> Status 16 (Sample on Hold)
        // -------------------------------------------------------------
        Map<String, Object> wo5 = new LinkedHashMap<>();
        wo5.put("LedgerTransactionID", 5);
        wo5.put("VisitNo", "WO-20261105-00004");
        wo5.put("LedgerTransactionNo", "WO-20261105-00004");
        wo5.put("PName", "Rahul Verma");
        wo5.put("PatientName", "Rahul Verma");
        wo5.put("PatientID", "LR2026-000004");
        wo5.put("PatientCode", "LR2026-000004");
        wo5.put("UHID", "LR2026-000004");
        wo5.put("Age", "45 Y");
        wo5.put("Gender", "Male");
        wo5.put("Mobile", "9844455566");
        wo5.put("DoctorName", "Dr. Rajesh Sharma, MD");
        wo5.put("DoctorID", 2);
        wo5.put("DoctorReferal", "2");
        wo5.put("Date", dateStr);
        wo5.put("dtEntry", dtEntry);
        wo5.put("PreSampleCollectionDate", dateStr);
        wo5.put("Status", 16); // 16 = Hold
        wo5.put("IsSampleCollected", "Y");
        wo5.put("SinNo", "BC20261105004");
        wo5.put("SINNo", "BC20261105004");
        wo5.put("BarcodeNo", "BC20261105004");
        wo5.put("BarcodeLogic", 1);
        wo5.put("BarcodeLogicID", 1);
        wo5.put("Department", "Microbiology");
        wo5.put("DepartmentID", 4);
        wo5.put("InvestigationID", 4);
        wo5.put("Test", "Dengue Serology NS1");
        wo5.put("TestName", "Dengue Serology NS1");
        wo5.put("ItemName", "Dengue Serology NS1");
        wo5.put("RateTypeName", "Standard Cash Rate");
        wo5.put("RateTypeCode", "CASH");
        wo5.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        wo5.put("CentreName", "Main Reference Lab");
        wo5.put("CentreID", 1);

        List<Map<String, Object>> items5 = new ArrayList<>();
        Map<String, Object> item5_1 = createTestItem(
                204, 5, 5, "WO-20261105-00004", "LR2026-000004", "Rahul Verma", "45 Y", "Male",
                4, "Dengue Serology NS1", 4, "Microbiology", 2, "Serum",
                "BC20261105004", 16, "Y", dateStr, dtEntry, "#ffc107"
        );
        item5_1.put("HoldReason", "Patient Fasting Verification Pending");
        items5.add(item5_1);

        INVESTIGATIONS.put(5, items5);
        updatePliDataList(wo5, items5);
        WORK_ORDERS.put(5, wo5);

        log.info("ClinicalSampleStore initialized with 5 distinct lifecycle work orders (Status 1, 2, 3, 4, 16)");
    }

    private static Map<String, Object> createTestItem(
            int testId, int investigationId, int ledgerId, String visitNo, String patientId,
            String pName, String age, String gender, int itemId, String testName,
            int deptId, String deptName, int sampleTypeId, String sampleTypeName,
            String sinNo, int status, String isCollected, String dateStr, String dtEntry, String color
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
        item.put("DoctorName", "Dr. Rajesh Sharma, MD");
        item.put("DoctorID", 2);
        item.put("DoctorReferal", "2");
        item.put("Department", deptName);
        item.put("DepartmentID", deptId);
        item.put("ItemName", testName);
        item.put("Test", testName);
        item.put("TestName", testName);
        item.put("testName", testName);
        item.put("PackageName", testName);
        item.put("InvestigationName", testName);
        item.put("BarcodeNo", sinNo);
        item.put("SinNo", sinNo);
        item.put("SINNo", sinNo);
        item.put("BarcodeLogic", 1);
        item.put("BarcodeLogicID", 1);
        item.put("SampleTypeID", sampleTypeId);
        item.put("SampleType", sampleTypeName);
        item.put("SampleTypeName", sampleTypeName);
        item.put("SampleName", sampleTypeName);
        item.put("Status", status);
        item.put("IsSampleCollected", isCollected);
        item.put("dtEntry", dtEntry);
        item.put("Date", dateStr);
        item.put("PreSampleCollectionDate", dateStr);
        item.put("CentreName", "Main Reference Lab");
        item.put("CentreID", 1);
        item.put("RateTypeName", "Standard Cash Rate");
        item.put("RateTypeCode", "CASH");
        item.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        item.put("HistoCytoSampleDetail", "1^0^0^");
        item.put("Container", "1");
        item.put("Slide", "0");
        item.put("Block", "0");
        item.put("IsChecked", 0);
        item.put("isReject", 0);
        item.put("Approved", 0);
        item.put("StatSample", 0);
        item.put("isUrgent", 0);
        item.put("HoldReason", "");
        item.put("SampleRemarks", "");
        item.put("Remarks", "");
        item.put("PackageName", "");
        item.put("awsKeys", "");
        item.put("IsPatientDocAvail", 0);
        item.put("MedicalHistoryCount", 0);
        item.put("IsVip", 0);
        item.put("isOutSource", 0);
        item.put("ColorName", color);
        item.put("CustomColor", color);
        item.put("TestIDHash", "hash" + testId);
        item.put("TestIdhash", "hash" + testId);
        item.put("isSelected", false);
        item.put("isSampleReCollection", 0);
        item.put("is_sra", 0);
        item.put("sra_status", "PENDING");
        item.put("sra_operator", "");
        item.put("sra_completed_at", "");
        item.put("sra_category", "");
        item.put("Segregation", 0);
        return item;
    }

    private static void updatePliDataList(Map<String, Object> wo, List<Map<String, Object>> items) {
        try {
            List<Map<String, Object>> pliList = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Map<String, Object> pli = new LinkedHashMap<>();
                pli.put("TestID", item.get("TestID"));
                pli.put("TestCode", item.get("TestID").toString());
                pli.put("Test", item.get("Test"));
                pli.put("TestName", item.get("TestName"));
                pli.put("ItemName", item.get("ItemName"));
                pli.put("InvestigationID", item.get("InvestigationID"));
                pli.put("ItemID", item.get("InvestigationID"));
                pli.put("DepartmentID", item.get("DepartmentID"));
                pli.put("Department", item.get("Department"));
                pli.put("Status", item.get("Status"));
                pli.put("IsUrgent", item.get("isUrgent"));
                pli.put("IsSampleCollected", item.get("IsSampleCollected"));
                pli.put("SampleName", item.get("SampleType"));
                pli.put("SampleTypeID", item.get("SampleTypeID"));
                pli.put("BarcodeNo", item.get("BarcodeNo"));
                pli.put("SINNo", item.get("SINNo"));
                pli.put("SinNo", item.get("SinNo"));
                pli.put("BarcodeLogic", item.get("BarcodeLogic"));
                pli.put("is_sra", item.get("is_sra"));
                pli.put("sra_status", item.get("sra_status"));
                pli.put("sra_category", item.get("sra_category"));
                pli.put("sra_completed_at", item.get("sra_completed_at"));
                pli.put("sra_operator", item.get("sra_operator"));
                pli.put("Segregation", item.get("Segregation"));
                pli.put("SampleInTransit", 0);
                pliList.add(pli);
            }
            wo.put("PliDataList", OBJECT_MAPPER.writeValueAsString(pliList));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize PliDataList", e);
        }
    }

    // -------------------------------------------------------------
    // Sample Collection Queries & Mutations
    // -------------------------------------------------------------
    public static synchronized List<Map<String, Object>> getSampleCollection(Object statusFilter) {
        Integer targetStatus = null;
        if (statusFilter != null && !statusFilter.toString().equalsIgnoreCase("All")
                && !statusFilter.toString().trim().isEmpty() && !statusFilter.toString().equals("0")) {
            try {
                targetStatus = Integer.parseInt(statusFilter.toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        List<Map<String, Object>> records = new ArrayList<>();
        for (Map<String, Object> wo : WORK_ORDERS.values()) {
            int currentStatus = Integer.parseInt(wo.get("Status").toString());
            if (targetStatus == null || targetStatus == 0 || targetStatus == currentStatus) {
                records.add(new LinkedHashMap<>(wo));
            }
        }
        return records;
    }

    public static synchronized List<Map<String, Object>> searchInvestigation(int ledgerId) {
        List<Map<String, Object>> items = INVESTIGATIONS.getOrDefault(ledgerId, Collections.emptyList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : items) {
            result.add(new LinkedHashMap<>(item));
        }
        return result;
    }

    public static synchronized Map<String, Object> collectSamples(List<?> rawData) {
        if (rawData == null || rawData.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "No sample data provided");
            return err;
        }

        // Check if all requested items are already collected (Status >= 2)
        boolean hasPending = false;
        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");

                int targetLedgerId = 1;
                if (ledgerIdObj != null) {
                    try { targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim()); } catch (NumberFormatException ignored) {}
                }
                int testId = 0;
                if (testIdObj != null) {
                    try { testId = Integer.parseInt(testIdObj.toString().trim()); } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            int currentStatus = 0;
                            if (inv.get("Status") != null) {
                                try { currentStatus = Integer.parseInt(inv.get("Status").toString().trim()); } catch (NumberFormatException ignored) {}
                            }
                            if (currentStatus < 2) {
                                hasPending = true;
                            }
                        }
                    }
                }
            }
        }

        if (!hasPending) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "This Barcode already collect");
            return err;
        }

        String primaryBarcode = "";
        int targetLedgerId = 1;
        String nowStr = LocalDateTime.now().format(DISPLAY_FMT);
        String dtEntry = LocalDateTime.now().format(DT_ENTRY_FMT);

        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");
                Object sinNoObj = item.get("SINNo");
                if (sinNoObj == null || sinNoObj.toString().trim().isEmpty()) {
                    sinNoObj = item.get("BarcodeNo");
                }
                String sinNo = sinNoObj != null ? sinNoObj.toString().trim() : "";
                if (!sinNo.isEmpty() && primaryBarcode.isEmpty()) {
                    primaryBarcode = sinNo;
                }

                if (ledgerIdObj != null) {
                    try {
                        targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                int testId = 0;
                if (testIdObj != null) {
                    try {
                        testId = Integer.parseInt(testIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            inv.put("Status", 2); // Status 2 = Sample Collected
                            inv.put("IsSampleCollected", "Y");
                            inv.put("is_sra", 0);
                            inv.put("sra_status", "PENDING");
                            inv.put("sra_category", "");
                            inv.put("sra_operator", "");
                            inv.put("sra_completed_at", "");
                            inv.put("PreSampleCollectionDate", nowStr);
                            inv.put("Date", nowStr);
                            inv.put("dtEntry", dtEntry);
                            inv.put("ColorName", "#ffa500");
                            inv.put("CustomColor", "#ffa500");
                            if (!sinNo.isEmpty()) {
                                inv.put("SINNo", sinNo);
                                inv.put("SinNo", sinNo);
                                inv.put("BarcodeNo", sinNo);
                            }
                            inv.put("isSelected", true);
                        }
                    }
                }
            }
        }

        Map<String, Object> wo = WORK_ORDERS.get(targetLedgerId);
        if (wo != null) {
            wo.put("Status", 2);
            wo.put("IsSampleCollected", "Y");
            wo.put("is_sra", 0);
            wo.put("sra_status", "PENDING");
            wo.put("sra_category", "");
            wo.put("sra_operator", "");
            wo.put("sra_completed_at", "");
            wo.put("PreSampleCollectionDate", nowStr);
            if (!primaryBarcode.isEmpty()) {
                wo.put("SinNo", primaryBarcode);
                wo.put("SINNo", primaryBarcode);
                wo.put("BarcodeNo", primaryBarcode);
            }
            List<Map<String, Object>> currentItems = INVESTIGATIONS.get(targetLedgerId);
            if (currentItems != null) {
                updatePliDataList(wo, currentItems);
            }
        }

        recordAudit("SAMPLE_COLLECTED", primaryBarcode, "Sample successfully collected at phlebotomy", "Phlebotomist", true);

        recordAudit("DEPARTMENT_RECEIVED", "Batch", "Department Sample Received Successfully", "Lab Technician", true);

        Map<String, Object> ok = new HashMap<>();
        ok.put("status", true);
        ok.put("success", true);
        ok.put("message", "Specimen collected successfully for Work Order.");
        return ok;
    }

    public static synchronized void rejectSamples(List<?> rawData) {
        if (rawData == null || rawData.isEmpty()) return;

        int targetLedgerId = 1;
        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");
                Object reasonObj = item.get("Reason");
                if (reasonObj == null || reasonObj.toString().trim().isEmpty()) {
                    reasonObj = item.get("CustomReason");
                }
                String reason = reasonObj != null ? reasonObj.toString().trim() : "Rejected";

                if (ledgerIdObj != null) {
                    try {
                        targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                int testId = 0;
                if (testIdObj != null) {
                    try {
                        testId = Integer.parseInt(testIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            inv.put("Status", 4); // Status 4 = Sample Rejected
                            inv.put("IsSampleCollected", "R");
                            inv.put("SampleRemarks", reason);
                            inv.put("Remarks", reason);
                            inv.put("isReject", 1);
                            inv.put("isSampleReCollection", 1);
                            inv.put("ColorName", "#B0C4DE");
                            inv.put("CustomColor", "#B0C4DE");
                        }
                    }
                }
            }
        }

        Map<String, Object> wo = WORK_ORDERS.get(targetLedgerId);
        if (wo != null) {
            wo.put("Status", 4);
            wo.put("IsSampleCollected", "R");
            List<Map<String, Object>> currentItems = INVESTIGATIONS.get(targetLedgerId);
            if (currentItems != null) {
                updatePliDataList(wo, currentItems);
            }
        }
    }

    public static synchronized void holdSample(List<?> rawData) {
        if (rawData == null || rawData.isEmpty()) return;

        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");
                Object reasonObj = item.get("HoldReason");
                String reason = reasonObj != null ? reasonObj.toString().trim() : "Sample Put On Hold";

                int targetLedgerId = 1;
                if (ledgerIdObj != null) {
                    try {
                        targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                int testId = 0;
                if (testIdObj != null) {
                    try {
                        testId = Integer.parseInt(testIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            inv.put("Status", 16); // Status 16 = Hold
                            inv.put("HoldReason", reason);
                            inv.put("ColorName", "#ffc107");
                            inv.put("CustomColor", "#ffc107");
                        }
                    }
                }

                Map<String, Object> wo = WORK_ORDERS.get(targetLedgerId);
                if (wo != null) {
                    wo.put("Status", 16);
                    List<Map<String, Object>> currentItems = INVESTIGATIONS.get(targetLedgerId);
                    if (currentItems != null) {
                        updatePliDataList(wo, currentItems);
                    }
                }
            }
        }
    }

    public static synchronized void unHoldSample(List<?> rawData) {
        if (rawData == null || rawData.isEmpty()) return;

        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");

                int targetLedgerId = 1;
                if (ledgerIdObj != null) {
                    try {
                        targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                int testId = 0;
                if (testIdObj != null) {
                    try {
                        testId = Integer.parseInt(testIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            inv.put("Status", 2); // Revert back to Collected / Pending Dept Receive
                            inv.put("HoldReason", "");
                            inv.put("ColorName", "#ffa500");
                            inv.put("CustomColor", "#ffa500");
                        }
                    }
                }

                Map<String, Object> wo = WORK_ORDERS.get(targetLedgerId);
                if (wo != null) {
                    wo.put("Status", 2);
                    List<Map<String, Object>> currentItems = INVESTIGATIONS.get(targetLedgerId);
                    if (currentItems != null) {
                        updatePliDataList(wo, currentItems);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Department Receive Queries & Mutations
    // -------------------------------------------------------------
    public static synchronized Map<String, Object> getDepartmentReceive(Map<String, Object> request) {
        Object statusFilter = request != null ? request.get("SampleStatus") : null;
        Integer targetStatus = null;
        if (statusFilter != null && !statusFilter.toString().equalsIgnoreCase("All")
                && !statusFilter.toString().trim().isEmpty() && !statusFilter.toString().equals("0")) {
            try {
                targetStatus = Integer.parseInt(statusFilter.toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        String selectTypes = request != null && request.get("SelectTypes") != null ? request.get("SelectTypes").toString().trim() : "";
        String itemValue = request != null && request.get("ItemValue") != null ? request.get("ItemValue").toString().trim().toLowerCase() : "";

        List<?> deptIdList = null;
        if (request != null && request.get("DepartmentID") instanceof List<?>) {
            deptIdList = (List<?>) request.get("DepartmentID");
        }

        List<Map<String, Object>> filteredTests = new ArrayList<>();
        Set<String> uniqueVisitNos = new HashSet<>();

        // Loop through all investigations across all ledger transactions
        for (List<Map<String, Object>> items : INVESTIGATIONS.values()) {
            for (Map<String, Object> test : items) {
                int status = Integer.parseInt(test.get("Status").toString());

                // Filter by targetStatus (2 = Collected, 3 = Received, 4 = Rejected, 16 = Hold)
                if (targetStatus == null || targetStatus == 0) {
                    // STRICT DOWNSTREAM EXCLUSION:
                    // Status 1 = Not Collected (stays in sample collection)
                    // Status 4 = Rejected (rejected samples MUST NOT appear in downstream screens)
                    if (status == 1 || status == 4) {
                        continue;
                    }
                } else if (status != targetStatus) {
                    continue;
                }

                // SRA GATE ENFORCEMENT (Defect 1):
                // In Department Receive, any sample with Status 2 (Collected) MUST have completed SRA (is_sra == 1).
                // Samples that have not completed SRA are strictly excluded from Department Receive.
                Object isSraObj = test.get("is_sra");
                boolean isSraCompleted = isSraObj != null && (
                        Objects.equals(isSraObj, 1) ||
                        Objects.equals(isSraObj, "1") ||
                        "SRA_COMPLETED".equalsIgnoreCase(String.valueOf(test.get("sra_status")))
                );

                if (status == 2 && !isSraCompleted) {
                    continue; // Exclude non-SRA samples from Department Receive list
                }

                // Filter by SelectTypes & ItemValue if provided
                if (!itemValue.isEmpty()) {
                    if ("BarcodeNo".equalsIgnoreCase(selectTypes)) {
                        String barcode = test.get("BarcodeNo") != null ? test.get("BarcodeNo").toString().toLowerCase() : "";
                        String sinNo = test.get("SinNo") != null ? test.get("SinNo").toString().toLowerCase() : "";
                        if (!barcode.contains(itemValue) && !sinNo.contains(itemValue)) continue;
                    } else if ("PatientCode".equalsIgnoreCase(selectTypes) || "PatientID".equalsIgnoreCase(selectTypes)) {
                        String pid = test.get("PatientID") != null ? test.get("PatientID").toString().toLowerCase() : "";
                        if (!pid.contains(itemValue)) continue;
                    } else if ("PName".equalsIgnoreCase(selectTypes)) {
                        String pname = test.get("PName") != null ? test.get("PName").toString().toLowerCase() : "";
                        if (!pname.contains(itemValue)) continue;
                    } else {
                        // General fallback search
                        String barcode = test.get("BarcodeNo") != null ? test.get("BarcodeNo").toString().toLowerCase() : "";
                        String pid = test.get("PatientID") != null ? test.get("PatientID").toString().toLowerCase() : "";
                        String visit = test.get("VisitNo") != null ? test.get("VisitNo").toString().toLowerCase() : "";
                        if (!barcode.contains(itemValue) && !pid.contains(itemValue) && !visit.contains(itemValue)) continue;
                    }
                }

                // Filter by DepartmentID if specified
                if (deptIdList != null && !deptIdList.isEmpty()) {
                    Object deptIdObj = test.get("DepartmentID");
                    boolean matchDept = false;
                    for (Object d : deptIdList) {
                        if (d != null && d.toString().equals(deptIdObj != null ? deptIdObj.toString() : "")) {
                            matchDept = true;
                            break;
                        }
                    }
                    if (!matchDept) continue;
                }

                Map<String, Object> copy = new LinkedHashMap<>(test);
                // Ensure table keys are strictly populated
                copy.put("SinNo", test.get("SinNo") != null ? test.get("SinNo") : test.get("BarcodeNo"));
                copy.put("SINNo", test.get("SinNo") != null ? test.get("SinNo") : test.get("BarcodeNo"));
                copy.put("BarcodeNo", test.get("BarcodeNo") != null ? test.get("BarcodeNo") : test.get("SinNo"));
                copy.put("SampleName", test.get("SampleTypeName") != null ? test.get("SampleTypeName") : test.get("SampleType"));
                copy.put("SampleTypeName", test.get("SampleTypeName") != null ? test.get("SampleTypeName") : test.get("SampleType"));
                copy.put("is_sra", isSraCompleted ? 1 : 0);
                copy.put("sra_status", test.getOrDefault("sra_status", isSraCompleted ? "SRA_COMPLETED" : "PENDING"));
                copy.put("sra_category", test.getOrDefault("sra_category", "In-House"));
                copy.put("sra_operator", test.getOrDefault("sra_operator", ""));
                copy.put("sra_completed_at", test.getOrDefault("sra_completed_at", ""));
                copy.put("IsChecked", 0);
                copy.put("isReject", 0);
                filteredTests.add(copy);

                if (copy.get("VisitNo") != null) {
                    uniqueVisitNos.add(copy.get("VisitNo").toString());
                }
            }
        }

        Map<String, Object> dataCounts = new HashMap<>();
        dataCounts.put("totalRecords", filteredTests.size());
        dataCounts.put("patientCount", uniqueVisitNos.size());

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", filteredTests);
        response.put("data", dataCounts);
        return response;
    }

    public static synchronized Map<String, Object> receiveDepartment(List<?> rawData) {
        if (rawData == null || rawData.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "No sample data provided");
            return err;
        }

        boolean anyPendingReceive = false;
        boolean anyAlreadyReceived = false;

        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");

                int targetLedgerId = 1;
                if (ledgerIdObj != null) {
                    try { targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim()); } catch (NumberFormatException ignored) {}
                }
                int testId = 0;
                if (testIdObj != null) {
                    try { testId = Integer.parseInt(testIdObj.toString().trim()); } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            int status = 0;
                            if (inv.get("Status") != null) {
                                try { status = Integer.parseInt(inv.get("Status").toString().trim()); } catch (NumberFormatException ignored) {}
                            }
                            if (status >= 3) {
                                anyAlreadyReceived = true;
                            } else {
                                anyPendingReceive = true;
                            }
                        }
                    }
                }
            }
        }

        if (!anyPendingReceive && anyAlreadyReceived) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "This Barcode already receive");
            return err;
        }

        // SRA GATE VALIDATION (Defect 1):
        // Department Receive MUST NOT accept or process any sample until its SRA step is complete.
        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");

                int targetLedgerId = 1;
                if (ledgerIdObj != null) {
                    try { targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim()); } catch (NumberFormatException ignored) {}
                }
                int testId = 0;
                if (testIdObj != null) {
                    try { testId = Integer.parseInt(testIdObj.toString().trim()); } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            Object isSraObj = inv.get("is_sra");
                            boolean isSraCompleted = isSraObj != null && (
                                    Objects.equals(isSraObj, 1) ||
                                    Objects.equals(isSraObj, "1") ||
                                    "SRA_COMPLETED".equalsIgnoreCase(String.valueOf(inv.get("sra_status")))
                            );
                            if (!isSraCompleted) {
                                String bc = inv.get("BarcodeNo") != null ? inv.get("BarcodeNo").toString() : "";
                                recordAudit("ILLEGAL_TRANSITION_REJECTED", bc, "Sample must complete SRA before Department Receive.", "API/User", false);
                                Map<String, Object> err = new HashMap<>();
                                err.put("status", false);
                                err.put("success", false);
                                err.put("statusCode", 409);
                                err.put("message", "Sample must complete SRA before Department Receive.");
                                return err;
                            }
                        }
                    }
                }
            }
        }

        String nowStr = LocalDateTime.now().format(DISPLAY_FMT);
        String dtEntry = LocalDateTime.now().format(DT_ENTRY_FMT);

        for (Object obj : rawData) {
            if (obj instanceof Map<?, ?> item) {
                Object testIdObj = item.get("TestID");
                Object ledgerIdObj = item.get("LedgerTransactionID");

                int targetLedgerId = 1;
                if (ledgerIdObj != null) {
                    try {
                        targetLedgerId = Integer.parseInt(ledgerIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                int testId = 0;
                if (testIdObj != null) {
                    try {
                        testId = Integer.parseInt(testIdObj.toString().trim());
                    } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> invList = INVESTIGATIONS.get(targetLedgerId);
                if (invList != null) {
                    for (Map<String, Object> inv : invList) {
                        if (testId == 0 || Objects.equals(inv.get("TestID"), testId)) {
                            inv.put("Status", 3); // Status 3 = Department Received
                            inv.put("IsSampleCollected", "Y");
                            inv.put("DepartmentReceiveDate", nowStr);
                            inv.put("ColorName", "#008000");
                            inv.put("CustomColor", "#008000");
                        }
                    }
                }

                Map<String, Object> wo = WORK_ORDERS.get(targetLedgerId);
                if (wo != null) {
                    wo.put("Status", 3);
                    List<Map<String, Object>> currentItems = INVESTIGATIONS.get(targetLedgerId);
                    if (currentItems != null) {
                        updatePliDataList(wo, currentItems);
                    }
                }
            }
        }

        recordAudit("DEPARTMENT_RECEIVED", "Batch", "Department Sample Received Successfully", "Lab Technician", true);

        Map<String, Object> ok = new HashMap<>();
        ok.put("status", true);
        ok.put("success", true);
        ok.put("message", "Department Sample Received Successfully");
        return ok;
    }

    public static synchronized Map<String, Object> receiveBarcode(String barcode) {
        String cleanBarcode = barcode != null ? barcode.trim() : "";
        if (cleanBarcode.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "Please enter barcode No.");
            return err;
        }

        List<Map<String, Object>> matchingTests = new ArrayList<>();
        boolean anyAlreadyReceived = false;
        boolean anyPendingReceive = false;

        for (List<Map<String, Object>> items : INVESTIGATIONS.values()) {
            for (Map<String, Object> test : items) {
                String bc = test.get("BarcodeNo") != null ? test.get("BarcodeNo").toString().trim() : "";
                String sin = test.get("SinNo") != null ? test.get("SinNo").toString().trim() : "";
                if (cleanBarcode.equalsIgnoreCase(bc) || cleanBarcode.equalsIgnoreCase(sin)) {
                    matchingTests.add(test);
                    int status = 0;
                    if (test.get("Status") != null) {
                        try {
                            status = Integer.parseInt(test.get("Status").toString().trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    if (status >= 3) {
                        anyAlreadyReceived = true;
                    } else if (status == 2) {
                        anyPendingReceive = true;
                    }
                }
            }
        }

        if (matchingTests.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "No sample found for barcode: " + cleanBarcode);
            return err;
        }

        // If all matching tests are ALREADY received (Status >= 3):
        if (!anyPendingReceive && anyAlreadyReceived) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("success", false);
            err.put("message", "This Barcode already receive");
            return err;
        }

        // SRA GATE VALIDATION (Defect 1):
        // Department Receive MUST NOT accept or process any sample until its SRA step is complete.
        for (Map<String, Object> test : matchingTests) {
            Object isSraObj = test.get("is_sra");
            boolean isSraCompleted = isSraObj != null && (
                    Objects.equals(isSraObj, 1) ||
                    Objects.equals(isSraObj, "1") ||
                    "SRA_COMPLETED".equalsIgnoreCase(String.valueOf(test.get("sra_status")))
            );
            if (!isSraCompleted) {
                recordAudit("ILLEGAL_TRANSITION_REJECTED", cleanBarcode, "Sample must complete SRA before Department Receive.", "API/User", false);
                Map<String, Object> err = new HashMap<>();
                err.put("status", false);
                err.put("success", false);
                err.put("statusCode", 409);
                err.put("message", "Sample must complete SRA before Department Receive.");
                return err;
            }
        }

        List<Map<String, Object>> receivedList = new ArrayList<>();
        String nowStr = LocalDateTime.now().format(DISPLAY_FMT);

        for (Map<String, Object> test : matchingTests) {
            test.put("Status", 3); // Status 3 = Department Received
            test.put("DepartmentReceiveDate", nowStr);
            test.put("ColorName", "#008000");
            test.put("CustomColor", "#008000");
            receivedList.add(new LinkedHashMap<>(test));

            Object ledgerIdObj = test.get("LedgerTransactionID");
            if (ledgerIdObj != null) {
                try {
                    int lId = Integer.parseInt(ledgerIdObj.toString().trim());
                    Map<String, Object> wo = WORK_ORDERS.get(lId);
                    if (wo != null) {
                        wo.put("Status", 3);
                        List<Map<String, Object>> cur = INVESTIGATIONS.get(lId);
                        if (cur != null) updatePliDataList(wo, cur);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        recordAudit("DEPARTMENT_RECEIVED", cleanBarcode, "Barcode Received Successfully in Department", "Lab Technician", true);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Barcode Received Successfully in Department");
        response.put("data", receivedList);
        return response;
    }

    public static synchronized long getRejectCount() {
        return WORK_ORDERS.values().stream()
                .filter(wo -> Objects.equals(wo.get("Status"), 4))
                .count();
    }

    public static synchronized Map<String, Object> registerPatientWorkOrder(Map<String, Object> payload) {
        if (payload == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", false);
            err.put("message", "Payload is null");
            return err;
        }

        log.info("ClinicalSampleStore: Registering incoming work order: {}", payload);

        Map<String, Object> patientData = (payload.get("PatientData") instanceof Map<?, ?>) ? (Map<String, Object>) payload.get("PatientData") : Collections.emptyMap();
        Map<String, Object> ltData = (payload.get("LTData") instanceof Map<?, ?>) ? (Map<String, Object>) payload.get("LTData") : Collections.emptyMap();
        List<?> ploList = (payload.get("PLO") instanceof List<?>) ? (List<?>) payload.get("PLO") : Collections.emptyList();

        int ledgerId = LEDGER_SEQ.incrementAndGet();
        if (ltData.get("LedgerTransactionID") != null) {
            try {
                ledgerId = Integer.parseInt(ltData.get("LedgerTransactionID").toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        String visitNo = "WO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + String.format("%05d", ledgerId);
        if (ltData.get("LedgerTransactionNo") != null && !ltData.get("LedgerTransactionNo").toString().trim().isEmpty()) {
            visitNo = ltData.get("LedgerTransactionNo").toString().trim();
        } else if (ltData.get("VisitNo") != null && !ltData.get("VisitNo").toString().trim().isEmpty()) {
            visitNo = ltData.get("VisitNo").toString().trim();
        }

        String patientId = "LR" + LocalDate.now().getYear() + "-" + String.format("%06d", ledgerId);
        if (patientData.get("PatientID") != null && !patientData.get("PatientID").toString().trim().isEmpty()) {
            patientId = patientData.get("PatientID").toString().trim();
        }

        String pName = null;
        if (patientData.get("PName") != null && !patientData.get("PName").toString().trim().isEmpty() && !patientData.get("PName").toString().trim().equalsIgnoreCase("Unknown")) {
            pName = patientData.get("PName").toString().trim();
        } else if (patientData.get("PatientName") != null && !patientData.get("PatientName").toString().trim().isEmpty() && !patientData.get("PatientName").toString().trim().equalsIgnoreCase("Unknown")) {
            pName = patientData.get("PatientName").toString().trim();
        } else if (ltData.get("PName") != null && !ltData.get("PName").toString().trim().isEmpty() && !ltData.get("PName").toString().trim().equalsIgnoreCase("Unknown")) {
            pName = ltData.get("PName").toString().trim();
        } else if (ltData.get("PatientName") != null && !ltData.get("PatientName").toString().trim().isEmpty() && !ltData.get("PatientName").toString().trim().equalsIgnoreCase("Unknown")) {
            pName = ltData.get("PatientName").toString().trim();
        } else if (patientData.get("FirstName") != null && !patientData.get("FirstName").toString().trim().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (patientData.get("Title") != null && !patientData.get("Title").toString().trim().isEmpty()) {
                sb.append(patientData.get("Title").toString().trim()).append(" ");
            }
            sb.append(patientData.get("FirstName").toString().trim());
            if (patientData.get("MiddleName") != null && !patientData.get("MiddleName").toString().trim().isEmpty()) {
                sb.append(" ").append(patientData.get("MiddleName").toString().trim());
            }
            if (patientData.get("LastName") != null && !patientData.get("LastName").toString().trim().isEmpty()) {
                sb.append(" ").append(patientData.get("LastName").toString().trim());
            }
            pName = sb.toString().trim();
        }

        if (pName == null || pName.trim().isEmpty() || pName.equalsIgnoreCase("Unknown")) {
            pName = "Walk-in Patient";
        }

        String age = "30 Y";
        if (patientData.get("Age") != null && !patientData.get("Age").toString().trim().isEmpty()) {
            age = patientData.get("Age").toString().trim();
        } else if (patientData.get("AgeYear") != null) {
            age = patientData.get("AgeYear").toString().trim() + " Y";
        }

        String gender = "Male";
        if (patientData.get("Gender") != null && !patientData.get("Gender").toString().trim().isEmpty()) {
            gender = patientData.get("Gender").toString().trim();
        }

        String mobile = "9876543210";
        if (patientData.get("Mobile") != null && !patientData.get("Mobile").toString().trim().isEmpty()) {
            mobile = patientData.get("Mobile").toString().trim();
        }

        String doctorName = "Dr. Rajesh Sharma, MD";
        int doctorId = 2;
        if (ltData.get("DoctorName") != null && !ltData.get("DoctorName").toString().trim().isEmpty()) {
            doctorName = ltData.get("DoctorName").toString().trim();
        }
        if (ltData.get("DoctorID") != null) {
            try { doctorId = Integer.parseInt(ltData.get("DoctorID").toString().trim()); } catch (NumberFormatException ignored) {}
        }

        int centreId = 1;
        if (ltData.get("CentreID") != null) {
            try { centreId = Integer.parseInt(ltData.get("CentreID").toString().trim()); } catch (NumberFormatException ignored) {}
        }

        LocalDateTime now = LocalDateTime.now();
        String dtEntry = now.format(DT_ENTRY_FMT);
        String dateStr = now.format(DISPLAY_FMT);

        // Build items from PLO
        List<Map<String, Object>> items = new ArrayList<>();
        if (!ploList.isEmpty()) {
            for (Object itemObj : ploList) {
                if (itemObj instanceof Map<?, ?> ploMap) {
                    int tId = TEST_SEQ.incrementAndGet();
                    int invId = 1;
                    if (ploMap.get("InvestigationID") != null) {
                        try { invId = Integer.parseInt(ploMap.get("InvestigationID").toString().trim()); } catch (NumberFormatException ignored) {}
                    }
                    String testName = "Complete Blood Count (CBC)";
                    if (ploMap.get("ItemName") != null && !ploMap.get("ItemName").toString().trim().isEmpty()) {
                        testName = ploMap.get("ItemName").toString().trim();
                    } else if (ploMap.get("TestName") != null && !ploMap.get("TestName").toString().trim().isEmpty()) {
                        testName = ploMap.get("TestName").toString().trim();
                    }

                    int sampleTypeId = 1;
                    String sampleTypeName = "Whole Blood EDTA";
                    String color = "#800080";
                    if (ploMap.get("SampleTypeID") != null) {
                        try { sampleTypeId = Integer.parseInt(ploMap.get("SampleTypeID").toString().trim()); } catch (NumberFormatException ignored) {}
                    }
                    if (sampleTypeId == 2) {
                        sampleTypeName = "Serum";
                        color = "#FFD700";
                    } else if (sampleTypeId == 3) {
                        sampleTypeName = "Fluoride Plasma";
                        color = "#808080";
                    }

                    int deptId = 1;
                    String deptName = "Hematology";
                    if (invId == 2 || sampleTypeId == 2) {
                        deptId = 2;
                        deptName = "Biochemistry";
                    }

                    Map<String, Object> testItem = createTestItem(
                            tId, invId, ledgerId, visitNo, patientId, pName, age, gender,
                            invId, testName, deptId, deptName, sampleTypeId, sampleTypeName,
                            "", 1, "N", dateStr, dtEntry, color
                    );
                    testItem.put("Mobile", mobile);
                    testItem.put("DoctorName", doctorName);
                    testItem.put("DoctorID", doctorId);
                    testItem.put("DoctorReferal", String.valueOf(doctorId));
                    testItem.put("CentreID", centreId);
                    items.add(testItem);
                }
            }
        }

        if (items.isEmpty()) {
            int tId = TEST_SEQ.incrementAndGet();
            Map<String, Object> testItem = createTestItem(
                    tId, 1, ledgerId, visitNo, patientId, pName, age, gender,
                    1, "Complete Blood Count (CBC)", 1, "Hematology", 1, "Whole Blood EDTA",
                    "", 1, "N", dateStr, dtEntry, "#800080"
            );
            testItem.put("Mobile", mobile);
            testItem.put("DoctorName", doctorName);
            testItem.put("DoctorID", doctorId);
            testItem.put("DoctorReferal", String.valueOf(doctorId));
            testItem.put("CentreID", centreId);
            items.add(testItem);
        }

        Map<String, Object> wo = new LinkedHashMap<>();
        wo.put("LedgerTransactionID", ledgerId);
        wo.put("VisitNo", visitNo);
        wo.put("LedgerTransactionNo", visitNo);
        wo.put("PName", pName);
        wo.put("PatientName", pName);
        wo.put("PatientID", patientId);
        wo.put("PatientCode", patientId);
        wo.put("UHID", patientId);
        wo.put("Age", age);
        wo.put("Gender", gender);
        wo.put("Mobile", mobile);
        wo.put("DoctorName", doctorName);
        wo.put("DoctorID", doctorId);
        wo.put("DoctorReferal", String.valueOf(doctorId));
        wo.put("Date", dateStr);
        wo.put("dtEntry", dtEntry);
        wo.put("PreSampleCollectionDate", dateStr);
        wo.put("Status", 1); // 1 = Not Collected
        wo.put("IsSampleCollected", "N");
        wo.put("SinNo", "");
        wo.put("SINNo", "");
        wo.put("BarcodeNo", "");
        wo.put("BarcodeLogic", 1);
        wo.put("BarcodeLogicID", 1);
        wo.put("Department", items.get(0).get("Department"));
        wo.put("DepartmentID", items.get(0).get("DepartmentID"));
        wo.put("InvestigationID", items.get(0).get("InvestigationID"));
        wo.put("Test", items.get(0).get("Test"));
        wo.put("TestName", items.get(0).get("TestName"));
        wo.put("ItemName", items.get(0).get("ItemName"));
        wo.put("RateTypeName", "Standard Cash Rate");
        wo.put("RateTypeCode", "CASH");
        wo.put("RateTypeNameWithCode", "Standard Cash Rate (CASH)");
        wo.put("CentreName", "Main Reference Lab");
        wo.put("CentreID", centreId);

        INVESTIGATIONS.put(ledgerId, items);
        updatePliDataList(wo, items);
        WORK_ORDERS.put(ledgerId, wo);

        log.info("ClinicalSampleStore: Successfully registered Work Order ID={}, VisitNo={}, TestsCount={}", ledgerId, visitNo, items.size());

        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("LedgerTransactionID", ledgerId);
        res.put("VisitNo", visitNo);
        res.put("PatientID", patientId);
        return res;
    }

    public static synchronized List<Map<String, Object>> getSraPendingData(Map<String, Object> request) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Object typeObj = request != null ? request.get("Type") : null;
        String typeStr = typeObj != null ? typeObj.toString().trim() : "1";

        for (List<Map<String, Object>> items : INVESTIGATIONS.values()) {
            for (Map<String, Object> test : items) {
                int status = Integer.parseInt(test.get("Status").toString());
                // SRA Pending view: Only collected samples (Status 2).
                // Status 1 (Not Collected) and Status 4 (Rejected) are STRICTLY excluded.
                if (status != 2) {
                    continue;
                }

                Object isSraObj = test.get("is_sra");
                boolean isSraCompleted = isSraObj != null && (
                        Objects.equals(isSraObj, 1) ||
                        Objects.equals(isSraObj, "1") ||
                        "SRA_COMPLETED".equalsIgnoreCase(String.valueOf(test.get("sra_status")))
                );

                // Type "1": SRA Pending All (only samples that have NOT yet completed SRA)
                // Type "2": Dept Receive Pending (only samples that HAVE completed SRA)
                if ("1".equals(typeStr) && isSraCompleted) {
                    continue;
                } else if ("2".equals(typeStr) && !isSraCompleted) {
                    continue;
                }

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("Centre", test.getOrDefault("CentreName", "Main Reference Lab"));
                row.put("Location", test.getOrDefault("CentreName", "Main Reference Lab"));
                row.put("LastDate", test.getOrDefault("Date", LocalDateTime.now().format(DT_ENTRY_FMT)));
                row.put("LastStatusDate", test.getOrDefault("Date", LocalDateTime.now().format(DT_ENTRY_FMT)));
                row.put("dtEntry", test.getOrDefault("dtEntry", LocalDateTime.now().format(DT_ENTRY_FMT)));
                String barcode = test.get("BarcodeNo") != null ? test.get("BarcodeNo").toString() : "";
                row.put("SINNo", barcode);
                row.put("sinNo", barcode);
                row.put("sINNo", barcode);
                row.put("BarcodeNo", barcode);
                row.put("barcodeNo", barcode);
                row.put("DispatchNo", "DSP-" + test.get("LedgerTransactionID"));
                row.put("DispatchCode", "DSP-" + test.get("LedgerTransactionID"));
                row.put("VisitNo", test.get("VisitNo"));
                row.put("VisitID", test.get("VisitNo"));
                row.put("visitID", test.get("VisitNo"));
                row.put("LedgerTransactionNo", test.get("VisitNo"));
                row.put("LedgerTransactionID", test.get("LedgerTransactionID"));
                row.put("UHID", test.get("PatientID"));
                row.put("PatientID", test.get("PatientID"));
                row.put("patientid", test.get("PatientID"));
                row.put("PName", test.get("PName"));
                row.put("PatientName", test.get("PName"));
                row.put("SampleType", test.get("SampleTypeName") != null ? test.get("SampleTypeName") : test.get("SampleType"));
                row.put("SampleTypeName", test.get("SampleTypeName") != null ? test.get("SampleTypeName") : test.get("SampleType"));
                row.put("Department", test.get("Department"));
                row.put("DepartmentID", test.get("DepartmentID"));
                row.put("TestName", test.get("TestName"));
                row.put("InvestigationName", test.get("TestName"));
                row.put("ItemName", test.get("ItemName"));
                row.put("TestID", test.get("TestID"));
                row.put("testid", test.get("TestID"));
                row.put("TimeDiffe1", 10);
                row.put("IsSampleCollected", "Y");
                row.put("isSampleCollected", "Y");
                int segregationVal = 0;
                if (test.get("Segregation") != null) {
                    try { segregationVal = Integer.parseInt(test.get("Segregation").toString().trim()); } catch (NumberFormatException ignored) {}
                }
                row.put("Segregation", segregationVal);
                row.put("is_sra", isSraCompleted ? 1 : 0);
                row.put("sra_status", test.getOrDefault("sra_status", isSraCompleted ? "SRA_COMPLETED" : "PENDING"));
                row.put("sra_category", test.getOrDefault("sra_category", segregationVal == 1 ? "Segregation" : "In-House"));
                row.put("sra_completed_at", test.getOrDefault("sra_completed_at", ""));
                row.put("sra_operator", test.getOrDefault("sra_operator", ""));
                row.put("Status", status);
                rows.add(row);
            }
        }
        return rows;
    }

    public static synchronized Map<String, Object> sraSampleSearch(String barcode) {
        Map<String, Object> response = new HashMap<>();
        String cleanBarcode = barcode != null ? barcode.trim() : "";
        if (cleanBarcode.isEmpty()) {
            response.put("status", false);
            response.put("success", false);
            response.put("message", "Please enter barcode No.");
            return response;
        }

        // Check if barcode has already been sorted in SRA
        if (SRA_SORTED_BARCODES.contains(cleanBarcode.toUpperCase())) {
            recordAudit("SRA_DUPLICATE_SORT_ATTEMPT", cleanBarcode, "Barcode already sorted in SRA", "Lab Tech (SRA)", false);
            response.put("status", false);
            response.put("success", false);
            response.put("message", "This Barcode already sort");
            return response;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        boolean found = false;
        boolean collected = false;
        String nowStr = LocalDateTime.now().format(DISPLAY_FMT);

        for (List<Map<String, Object>> items : INVESTIGATIONS.values()) {
            for (Map<String, Object> test : items) {
                String bc = test.get("BarcodeNo") != null ? test.get("BarcodeNo").toString().trim() : "";
                String sin = test.get("SinNo") != null ? test.get("SinNo").toString().trim() : "";
                if (cleanBarcode.equalsIgnoreCase(bc) || cleanBarcode.equalsIgnoreCase(sin)) {
                    found = true;
                    int status = 0;
                    if (test.get("Status") != null) {
                        try {
                            status = Integer.parseInt(test.get("Status").toString().trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    if (status >= 2) {
                        collected = true;
                    }

                    int segregationVal = 0;
                    if (test.get("Segregation") != null) {
                        try { segregationVal = Integer.parseInt(test.get("Segregation").toString().trim()); } catch (NumberFormatException ignored) {}
                    }
                    String category = (segregationVal == 1 || segregationVal == 2) ? "Segregation" : "In-House";

                    test.put("is_sra", 1);
                    test.put("sra_status", "SRA_COMPLETED");
                    test.put("sra_completed_at", nowStr);
                    test.put("sra_operator", "Lab Tech (SRA)");
                    test.put("sra_category", category);

                    Object ledgerIdObj = test.get("LedgerTransactionID");
                    if (ledgerIdObj != null) {
                        try {
                            int lId = Integer.parseInt(ledgerIdObj.toString().trim());
                            Map<String, Object> wo = WORK_ORDERS.get(lId);
                            if (wo != null) {
                                wo.put("is_sra", 1);
                                wo.put("sra_status", "SRA_COMPLETED");
                                wo.put("sra_completed_at", nowStr);
                                wo.put("sra_operator", "Lab Tech (SRA)");
                                wo.put("sra_category", category);
                            }
                        } catch (NumberFormatException ignored) {}
                    }

                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Centre", test.getOrDefault("CentreName", "Main Reference Lab"));
                    row.put("Location", test.getOrDefault("CentreName", "Main Reference Lab"));
                    row.put("LastDate", test.getOrDefault("Date", LocalDateTime.now().format(DT_ENTRY_FMT)));
                    row.put("LastStatusDate", test.getOrDefault("Date", LocalDateTime.now().format(DT_ENTRY_FMT)));
                    row.put("dtEntry", test.getOrDefault("dtEntry", LocalDateTime.now().format(DT_ENTRY_FMT)));
                    row.put("SINNo", bc.isEmpty() ? cleanBarcode : bc);
                    row.put("BarcodeNo", bc.isEmpty() ? cleanBarcode : bc);
                    row.put("VisitNo", test.get("VisitNo"));
                    row.put("LedgerTransactionNo", test.get("VisitNo"));
                    row.put("LedgerTransactionID", test.get("LedgerTransactionID"));
                    row.put("UHID", test.get("PatientID"));
                    row.put("PatientID", test.get("PatientID"));
                    row.put("PName", test.get("PName"));
                    row.put("PatientName", test.get("PName"));
                    row.put("SampleType", test.get("SampleTypeName") != null ? test.get("SampleTypeName") : test.get("SampleType"));
                    row.put("SampleTypeName", test.get("SampleTypeName") != null ? test.get("SampleTypeName") : test.get("SampleType"));
                    row.put("Department", test.get("Department"));
                    row.put("DepartmentID", test.get("DepartmentID"));
                    row.put("TestName", test.get("TestName"));
                    row.put("InvestigationName", test.get("TestName"));
                    row.put("ItemName", test.get("ItemName"));
                    row.put("TestID", test.get("TestID"));
                    row.put("TimeDiffe1", 10);
                    row.put("IsSampleCollected", "Y");
                    row.put("Segregation", segregationVal);
                    row.put("is_sra", 1);
                    row.put("sra_status", "SRA_COMPLETED");
                    row.put("sra_completed_at", nowStr);
                    row.put("sra_operator", "Lab Tech (SRA)");
                    row.put("sra_category", category);
                    row.put("Status", test.get("Status"));
                    result.add(row);
                }
            }
        }

        if (!found) {
            response.put("status", false);
            response.put("success", false);
            response.put("message", "No sample found for barcode: " + cleanBarcode);
            return response;
        }

        if (!collected) {
            recordAudit("SRA_UNCOLLECTED_SORT_ATTEMPT", cleanBarcode, "Attempted SRA sorting before sample collection", "Lab Tech (SRA)", false);
            response.put("status", false);
            response.put("success", false);
            response.put("message", "Sample not collected yet. Please collect sample first.");
            return response;
        }

        // Successfully sorted! Track barcode so re-sort attempt will fail with "This Barcode already sort"
        SRA_SORTED_BARCODES.add(cleanBarcode.toUpperCase());
        String cat = result.isEmpty() ? "In-House" : String.valueOf(result.get(0).getOrDefault("sra_category", "In-House"));
        recordAudit("SRA_COMPLETED", cleanBarcode, "Sample successfully sorted into " + cat, "Lab Tech (SRA)", true);

        response.put("status", true);
        response.put("success", true);
        response.put("message", result);
        return response;
    }
}
