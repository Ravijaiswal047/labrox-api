package com.labrox.sample.controller;

import com.labrox.sample.service.ClinicalSampleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SRAGateWorkflowTest {

    private LegacyDepartmentReceiveController departmentReceiveController;
    private LegacySRAController sraController;

    @BeforeEach
    void setUp() {
        ClinicalSampleStore.resetInitialData();
        departmentReceiveController = new LegacyDepartmentReceiveController();
        sraController = new LegacySRAController();
    }

    @Test
    @DisplayName("D1 Negative 1: Direct barcode receive rejects non-SRA sample with HTTP 409 Conflict")
    void testDepartmentReceiveRejectsNonSraBarcodeWith409() {
        // WO 2 ("BC20261105001") is in Status 2 (Collected), but has NOT passed SRA (is_sra = 0)
        Map<String, Object> req = new HashMap<>();
        req.put("BarcodeNo", "BC20261105001");

        ResponseEntity<Map<String, Object>> response = departmentReceiveController.sampleReceiveBarcode(req);
        assertEquals(409, response.getStatusCode().value(), "Server must reject non-SRA sample with HTTP 409 Conflict");
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Sample must complete SRA before Department Receive.", response.getBody().get("message"));

        // Verify audit log has recorded the rejected illegal transition
        boolean auditRecorded = ClinicalSampleStore.AUDIT_LOG.stream().anyMatch(entry ->
                "ILLEGAL_TRANSITION_REJECTED".equals(entry.get("action")) &&
                "BC20261105001".equals(entry.get("barcode")) &&
                Boolean.FALSE.equals(entry.get("success"))
        );
        assertTrue(auditRecorded, "Audit log must record the rejected transition attempt");
    }

    @Test
    @DisplayName("D1 Negative 2: Batch department receive rejects non-SRA sample with HTTP 409 Conflict")
    void testDepartmentReceiveRejectsNonSraBatchWith409() {
        Map<String, Object> testItem = new HashMap<>();
        testItem.put("TestID", 201);
        testItem.put("LedgerTransactionID", 2);

        Map<String, Object> req = new HashMap<>();
        req.put("data", List.of(testItem));

        ResponseEntity<Map<String, Object>> response = departmentReceiveController.saveDepartmentReceive(req);
        assertEquals(409, response.getStatusCode().value(), "Server must reject batch receive of non-SRA sample with HTTP 409");
        assertNotNull(response.getBody());
        assertEquals("Sample must complete SRA before Department Receive.", response.getBody().get("message"));
    }

    @Test
    @DisplayName("D1 Query: Department Receive list strictly excludes non-SRA collected samples")
    void testDepartmentReceiveListExcludesNonSraSamples() {
        Map<String, Object> req = new HashMap<>();
        req.put("SampleStatus", 2);

        ResponseEntity<Map<String, Object>> response = departmentReceiveController.getDepartmentReceive(req);
        assertEquals(200, response.getStatusCode().value());
        List<?> items = (List<?>) response.getBody().get("message");
        assertNotNull(items);

        // Assert that BC20261105001 (which has not passed SRA) is NOT in the list
        boolean containsNonSra = items.stream().anyMatch(item -> {
            if (item instanceof Map<?, ?> m) {
                return "BC20261105001".equalsIgnoreCase(String.valueOf(m.get("BarcodeNo")));
            }
            return false;
        });
        assertFalse(containsNonSra, "Non-SRA sample BC20261105001 must not be listed in Department Receive");
    }

    @Test
    @DisplayName("D1 Positive: SRA sort transitions sample to SRA_COMPLETED, enabling successful Department Receive")
    void testSuccessfulSraTransitionAndDepartmentReceive() {
        // Step 1: Scan barcode in SRA sorting stage
        Map<String, Object> sraReq = new HashMap<>();
        sraReq.put("barcodeNo", "BC20261105001");

        ResponseEntity<Map<String, Object>> sraRes = sraController.sampleSearch(sraReq);
        assertEquals(200, sraRes.getStatusCode().value());
        assertTrue((Boolean) sraRes.getBody().get("success"));

        List<?> sraData = (List<?>) sraRes.getBody().get("message");
        assertFalse(sraData.isEmpty());
        Map<?, ?> firstSra = (Map<?, ?>) sraData.get(0);
        assertEquals(1, firstSra.get("is_sra"));
        assertEquals("SRA_COMPLETED", firstSra.get("sra_status"));
        assertEquals("In-House", firstSra.get("sra_category"));

        // Step 2: Now that SRA is completed, Department Receive must accept the sample
        Map<String, Object> drReq = new HashMap<>();
        drReq.put("BarcodeNo", "BC20261105001");

        ResponseEntity<Map<String, Object>> drRes = departmentReceiveController.sampleReceiveBarcode(drReq);
        assertEquals(200, drRes.getStatusCode().value(), "Department Receive must accept SRA-completed sample with 200 OK");
        assertTrue((Boolean) drRes.getBody().get("success"));
        assertEquals("Barcode Received Successfully in Department", drRes.getBody().get("message"));

        // Step 3: Verify audit trail
        boolean sraAudit = ClinicalSampleStore.AUDIT_LOG.stream().anyMatch(e ->
                "SRA_COMPLETED".equals(e.get("action")) && "BC20261105001".equals(e.get("barcode")));
        boolean drAudit = ClinicalSampleStore.AUDIT_LOG.stream().anyMatch(e ->
                "DEPARTMENT_RECEIVED".equals(e.get("action")) && "BC20261105001".equals(e.get("barcode")));
        assertTrue(sraAudit, "Audit must record SRA completion");
        assertTrue(drAudit, "Audit must record Department Reception");
    }
}
