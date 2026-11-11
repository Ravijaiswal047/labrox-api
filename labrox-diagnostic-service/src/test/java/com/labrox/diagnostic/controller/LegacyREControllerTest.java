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
        req.put("SelectTypes", "BarcodeNo");
        req.put("ItemValue", "BAR20261104001");

        ResponseEntity<Map<String, Object>> response = controller.getResultEntry(req);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("status"));

        List<?> list = (List<?>) response.getBody().get("message");
        assertNotNull(list);
        assertFalse(list.isEmpty());

        Map<?, ?> first = (Map<?, ?>) list.get(0);
        assertEquals(101, first.get("TestID"));
        assertEquals("BAR20261104001", first.get("BarcodeNo"));
    }

    @Test
    @DisplayName("RE: GetResultEntryData returns observations with reference ranges")
    void testGetResultEntryData() {
        Map<String, Object> req = new HashMap<>();
        req.put("TestID", 101);

        ResponseEntity<Map<String, Object>> response = controller.getResultEntryData(req);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("status"));

        List<?> list = (List<?>) response.getBody().get("message");
        assertNotNull(list);
        assertEquals(5, list.size()); // Hb, WBC, Platelets, PCV, RBC
    }

    @Test
    @DisplayName("RE: SaveResultEntry and ForwardToDoctor return success envelopes")
    void testSaveAndForward() {
        ResponseEntity<Map<String, Object>> saveRes = controller.saveResultEntry(new HashMap<>());
        assertEquals(200, saveRes.getStatusCode().value());
        assertEquals("Result Entry Saved Successfully", saveRes.getBody().get("message"));

        ResponseEntity<Map<String, Object>> fwdRes = controller.forwardToDoctor(new HashMap<>());
        assertEquals(200, fwdRes.getStatusCode().value());
        assertEquals("Forwarded to Doctor for Approval Successfully", fwdRes.getBody().get("message"));
    }
}
