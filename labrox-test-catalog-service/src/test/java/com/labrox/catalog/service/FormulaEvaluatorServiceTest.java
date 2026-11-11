package com.labrox.catalog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FormulaEvaluatorServiceTest {

    private FormulaEvaluatorService formulaEvaluator;

    @BeforeEach
    void setUp() {
        formulaEvaluator = new FormulaEvaluatorService();
    }

    @Test
    @DisplayName("Evaluate Indirect Bilirubin: Total Bilirubin (1.20) - Direct Bilirubin (0.40) = 0.80")
    void testIndirectBilirubin() {
        Map<String, BigDecimal> inputs = new HashMap<>();
        inputs.put("TotalBilirubin", new BigDecimal("1.20"));
        inputs.put("DirectBilirubin", new BigDecimal("0.40"));

        BigDecimal result = formulaEvaluator.evaluateFormula("INDIRECT_BILIRUBIN", inputs);
        assertNotNull(result);
        assertEquals(new BigDecimal("0.80"), result);
    }

    @Test
    @DisplayName("Evaluate Globulin: Total Protein (7.50) - Albumin (4.20) = 3.30")
    void testGlobulin() {
        Map<String, BigDecimal> inputs = new HashMap<>();
        inputs.put("TotalProtein", new BigDecimal("7.50"));
        inputs.put("Albumin", new BigDecimal("4.20"));

        BigDecimal result = formulaEvaluator.evaluateFormula("GLOBULIN", inputs);
        assertNotNull(result);
        assertEquals(new BigDecimal("3.30"), result);
    }

    @Test
    @DisplayName("Evaluate A/G Ratio: Albumin (4.20) / Globulin (3.30) = 1.27")
    void testAGRatio() {
        Map<String, BigDecimal> inputs = new HashMap<>();
        inputs.put("Albumin", new BigDecimal("4.20"));
        inputs.put("Globulin", new BigDecimal("3.30"));

        BigDecimal result = formulaEvaluator.evaluateFormula("AG_RATIO", inputs);
        assertNotNull(result);
        assertEquals(new BigDecimal("1.27"), result);
    }

    @Test
    @DisplayName("Evaluate Anion Gap: Sodium (140.0) - (Chloride (102.0) + Bicarbonate (24.0)) = 14.0")
    void testAnionGap() {
        Map<String, BigDecimal> inputs = new HashMap<>();
        inputs.put("Sodium", new BigDecimal("140.0"));
        inputs.put("Chloride", new BigDecimal("102.0"));
        inputs.put("Bicarbonate", new BigDecimal("24.0"));

        BigDecimal result = formulaEvaluator.evaluateFormula("ANION_GAP", inputs);
        assertNotNull(result);
        assertEquals(new BigDecimal("14.0"), result);
    }
}
