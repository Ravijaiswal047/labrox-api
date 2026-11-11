package com.labrox.approval.controller;

import com.labrox.approval.service.ApprovalWorkflowService;
import com.labrox.approval.service.ElectronicSignatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LegacyApprovalControllerTest {

    private ApprovalWorkflowService approvalService;
    private ElectronicSignatureService signatureService;
    private LegacyChangeResultStatusController statusController;
    private LegacyDoctorApprovalController doctorController;

    @BeforeEach
    void setUp() {
        approvalService = Mockito.mock(ApprovalWorkflowService.class);
        signatureService = Mockito.mock(ElectronicSignatureService.class);
        statusController = new LegacyChangeResultStatusController(approvalService, signatureService);
        doctorController = new LegacyDoctorApprovalController();
    }

    @Test
    @DisplayName("Approval: getChangesReportStatus returns tests ready for sign-off")
    void testGetChangesReportStatus() {
        Map<String, Object> req = new HashMap<>();
        req.put("Status", 4);

        ResponseEntity<Map<String, Object>> res = statusController.getChangesReportStatus(req);
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));

        List<?> list = (List<?>) res.getBody().get("message");
        assertNotNull(list);
        assertFalse(list.isEmpty());

        Map<?, ?> first = (Map<?, ?>) list.get(0);
        assertEquals(101, first.get("TestID"));
        assertEquals("BAR20261104001", first.get("BarcodeNo"));
    }

    @Test
    @DisplayName("Approval: UpdateChangeResultStatus returns success envelope")
    void testUpdateChangeResultStatus() {
        ResponseEntity<Map<String, Object>> res = statusController.updateChangeResultStatus(new HashMap<>());
        assertEquals(200, res.getStatusCode().value());
        assertEquals("Status Updated Successfully", res.getBody().get("message"));
    }

    @Test
    @DisplayName("DoctorApproval: PatientSearch returns patient list")
    void testDoctorApprovalPatientSearch() {
        ResponseEntity<Map<String, Object>> res = doctorController.patientSearch(new HashMap<>());
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));
        assertNotNull(res.getBody().get("message"));
    }
}
