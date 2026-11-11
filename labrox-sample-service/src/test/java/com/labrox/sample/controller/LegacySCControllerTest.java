package com.labrox.sample.controller;

import com.labrox.sample.service.BarcodeGeneratorService;
import com.labrox.sample.service.SampleCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LegacySCControllerTest {

    private SampleCollectionService sampleService;
    private BarcodeGeneratorService barcodeGenerator;
    private LegacySCController controller;
    private LegacySampleReceiveBarcodeController receiveController;

    @BeforeEach
    void setUp() {
        sampleService = Mockito.mock(SampleCollectionService.class);
        barcodeGenerator = Mockito.mock(BarcodeGeneratorService.class);
        controller = new LegacySCController(sampleService, barcodeGenerator);
        receiveController = new LegacySampleReceiveBarcodeController();
    }

    @Test
    @DisplayName("SC: GetSampleCollection returns matching legacy envelope with PliDataList")
    void testGetSampleCollection() {
        Map<String, Object> req = new HashMap<>();
        req.put("SelectTypes", "BarcodeNo");
        req.put("ItemValue", "BAR20261104001");

        ResponseEntity<Map<String, Object>> response = controller.getSampleCollection(req);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("status"));
        assertTrue((Boolean) response.getBody().get("success"));

        List<?> list = (List<?>) response.getBody().get("message");
        assertNotNull(list);
        assertFalse(list.isEmpty());

        Map<?, ?> first = (Map<?, ?>) list.get(0);
        assertEquals("WO-20261104-00109", first.get("VisitNo"));
        assertEquals("BAR20261104001", first.get("BarcodeNo"));
        assertNotNull(first.get("PliDataList"));
    }

    @Test
    @DisplayName("SC: SearchInvestigation returns pending test list for LedgerTransaction")
    void testSearchInvestigation() {
        Map<String, Object> req = new HashMap<>();
        req.put("LedgerTransactionID", 1);

        ResponseEntity<Map<String, Object>> response = controller.searchInvestigation(req);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("status"));

        List<?> list = (List<?>) response.getBody().get("message");
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("SampleReceiveBarcode: Search and Save receive returns success envelope")
    void testSampleReceiveBarcode() {
        Map<String, Object> searchReq = new HashMap<>();
        searchReq.put("SearchData", "BAR20261104001");

        ResponseEntity<Map<String, Object>> searchRes = receiveController.searchSampleReceive(searchReq);
        assertEquals(200, searchRes.getStatusCode().value());
        assertTrue((Boolean) searchRes.getBody().get("status"));

        ResponseEntity<Map<String, Object>> saveRes = receiveController.saveSampleReceive(List.of(searchReq));
        assertEquals(200, saveRes.getStatusCode().value());
        assertEquals("Sample Received Successfully", saveRes.getBody().get("message"));
    }
}
