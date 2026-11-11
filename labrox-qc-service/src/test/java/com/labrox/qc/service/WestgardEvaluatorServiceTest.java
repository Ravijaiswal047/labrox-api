package com.labrox.qc.service;

import com.labrox.qc.dto.WestgardEvaluationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WestgardEvaluatorServiceTest {

    private WestgardEvaluatorService evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new WestgardEvaluatorService();
    }

    @Test
    @DisplayName("Normal measurement within 1SD: Z=0.5 -> IN_CONTROL")
    void testInControl() {
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("0.50"), Collections.emptyList());
        assertEquals("IN_CONTROL", result.getStatus());
        assertNull(result.getViolatedRule());
        assertFalse(result.isLockout());
    }

    @Test
    @DisplayName("Warning Rule 1_2s: Z=2.2 -> WARNING (no lockout)")
    void testRule1_2s() {
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("2.20"), Collections.emptyList());
        assertEquals("WARNING", result.getStatus());
        assertEquals("1_2s", result.getViolatedRule());
        assertFalse(result.isLockout());
    }

    @Test
    @DisplayName("Rejection Rule 1_3s: Z=3.4 -> REJECTED & LOCKOUT")
    void testRule1_3s() {
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("3.40"), Collections.emptyList());
        assertEquals("REJECTED", result.getStatus());
        assertEquals("1_3s", result.getViolatedRule());
        assertTrue(result.isLockout());
    }

    @Test
    @DisplayName("Rejection Rule 2_2s: Z0=2.3 and Z1=2.1 -> REJECTED & LOCKOUT")
    void testRule2_2s() {
        List<BigDecimal> history = List.of(new BigDecimal("2.10"));
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("2.30"), history);
        assertEquals("REJECTED", result.getStatus());
        assertEquals("2_2s", result.getViolatedRule());
        assertTrue(result.isLockout());
    }

    @Test
    @DisplayName("Rejection Rule R_4s: Z0=2.2 and Z1=-2.1 (range 4.3s) -> REJECTED & LOCKOUT")
    void testRuleR_4s() {
        List<BigDecimal> history = List.of(new BigDecimal("-2.10"));
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("2.20"), history);
        assertEquals("REJECTED", result.getStatus());
        assertEquals("R_4s", result.getViolatedRule());
        assertTrue(result.isLockout());
    }

    @Test
    @DisplayName("Rejection Rule 4_1s: 4 consecutive runs > 1s -> REJECTED")
    void testRule4_1s() {
        List<BigDecimal> history = List.of(
                new BigDecimal("1.20"),
                new BigDecimal("1.40"),
                new BigDecimal("1.10")
        );
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("1.30"), history);
        assertEquals("REJECTED", result.getStatus());
        assertEquals("4_1s", result.getViolatedRule());
    }

    @Test
    @DisplayName("Rejection Rule 10_x: 10 consecutive runs on positive side -> REJECTED")
    void testRule10_x() {
        List<BigDecimal> history = List.of(
                new BigDecimal("0.80"), new BigDecimal("0.60"), new BigDecimal("0.40"),
                new BigDecimal("0.90"), new BigDecimal("0.50"), new BigDecimal("0.70"),
                new BigDecimal("0.30"), new BigDecimal("0.85"), new BigDecimal("0.65")
        );
        WestgardEvaluationResult result = evaluator.evaluate(new BigDecimal("0.55"), history);
        assertEquals("REJECTED", result.getStatus());
        assertEquals("10_x", result.getViolatedRule());
    }
}
