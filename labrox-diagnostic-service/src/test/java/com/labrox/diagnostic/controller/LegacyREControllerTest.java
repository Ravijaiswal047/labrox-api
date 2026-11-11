package com.labrox.diagnostic.controller;

import com.labrox.diagnostic.service.DeltaCheckService;
import com.labrox.diagnostic.service.ResultEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LegacyREControllerTest {

    private ResultEntryService resultEntryService;
    private DeltaCheckService deltaCheckService;
    private LegacyREController controller;

    @BeforeEach
    void setUp() {
        resultEntryService = Mockito.mock(ResultEntryService.class);
        deltaCheckService = Mockito.mock(DeltaCheckService.class);
        controller = new LegacyREController(resultEntryService, deltaCheckService);
    }

    @Test
    @DisplayName("RE: GetResultEntry returns tests pending result entry")
    void testGetResultEntry() {
        Map<String, Object> req = new HashMap<>();

        ResponseEntity<Map<String, Object>> response = controller.getResultEntry(req);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("status"));

        List<?> list = (List<?>) response.getBody().get("message");
        assertNotNull(list);
    }

    @Test
    @DisplayName("RE: GetResultEntryData returns observations with reference ranges")
    void testGetResultEntryData() {
        Map<String, Object> req = new HashMap<>();
        req.put("TestID", 101);

        ResponseEntity<Map<String, Object>> response = controller.getResultEntryData(req);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("status"));

        Map<?, ?> msgMap = (Map<?, ?>) response.getBody().get("message");
        assertNotNull(msgMap);
        List<?> obs = (List<?>) msgMap.get("message");
        assertNotNull(obs);
        assertFalse(obs.isEmpty());
    }

    @Test
    @DisplayName("RE: SaveResultEntry without doctor returns 400 Bad Request with field error")
    void testSaveResultEntryWithoutDoctorFails() {
        Map<String, Object> req = new HashMap<>();
        req.put("ResultStatus", "Save");

        ResponseEntity<Map<String, Object>> res = controller.saveResultEntry(req);
        assertEquals(400, res.getStatusCode().value());
        assertFalse((Boolean) res.getBody().get("success"));
        assertEquals("Doctor selection is mandatory. Please select a Doctor before saving.", res.getBody().get("message"));
        assertNotNull(res.getBody().get("correlationId"));
        assertNotNull(res.getBody().get("fieldErrors"));
    }

    @Test
    @DisplayName("RE: SaveResultEntry with doctor returns 200/201 success envelope")
    void testSaveResultEntryWithDoctorSucceeds() {
        Map<String, Object> req = new HashMap<>();
        req.put("doctorId", 1);
        req.put("ResultStatus", "Save");

        ResponseEntity<Map<String, Object>> res = controller.saveResultEntry(req);
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("success"));
        assertEquals("Result Entry Saved Successfully", res.getBody().get("message"));
        assertEquals("1", res.getBody().get("doctorId"));
        assertNotNull(res.getBody().get("correlationId"));
    }

    @Test
    @DisplayName("RE: Approve results with doctor returns 200 success envelope")
    void testApproveResultsWithDoctorSucceeds() {
        Map<String, Object> req = new HashMap<>();
        req.put("ApprovedBy", "2");
        req.put("ResultStatus", "Approved");

        ResponseEntity<Map<String, Object>> res = controller.saveResultEntry(req);
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("success"));
        assertEquals("Results Approved Successfully", res.getBody().get("message"));
        assertEquals("2", res.getBody().get("doctorId"));
    }

    @Test
    @DisplayName("RE: ForwardToDoctor returns success envelope")
    void testForwardToDoctor() {
        ResponseEntity<Map<String, Object>> fwdRes = controller.forwardToDoctor(new HashMap<>());
        assertEquals(200, fwdRes.getStatusCode().value());
        assertEquals("Forwarded to Doctor for Approval Successfully", fwdRes.getBody().get("message"));
    }
}
