package com.labrox.catalog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LegacyCatalogControllerTest {

    private LegacyInvestigationsController invController;
    private LegacyRateListController rateController;

    @BeforeEach
    void setUp() {
        invController = new LegacyInvestigationsController();
        rateController = new LegacyRateListController();
    }

    @Test
    @DisplayName("Investigations: GetInvestigations and BindInvestigationList return catalog items")
    void testGetInvestigations() {
        ResponseEntity<Map<String, Object>> res = invController.getInvestigations(new HashMap<>());
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));

        List<?> list = (List<?>) res.getBody().get("message");
        assertNotNull(list);
        assertEquals(5, list.size()); // CBC, Lipid, FBS, LFT, KFT

        ResponseEntity<Map<String, Object>> bindRes = invController.bindInvestigationList(new HashMap<>());
        assertEquals(200, bindRes.getStatusCode().value());
        assertTrue((Boolean) bindRes.getBody().get("status"));
    }

    @Test
    @DisplayName("RateList: GetRateListWithType and SaveRateList behave correctly")
    void testRateList() {
        ResponseEntity<Map<String, Object>> res = rateController.getRateListWithType(new HashMap<>());
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));

        List<?> list = (List<?>) res.getBody().get("message");
        assertNotNull(list);
        assertFalse(list.isEmpty());

        ResponseEntity<Map<String, Object>> saveRes = rateController.saveRateList(new HashMap<>());
        assertEquals(200, saveRes.getStatusCode().value());
        assertEquals("Rate List Saved Successfully", saveRes.getBody().get("message"));
    }
}
