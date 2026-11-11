package com.labrox.diagnostic.service;

import com.labrox.diagnostic.dto.DeltaCheckResult;
import com.labrox.diagnostic.repository.PatientLabObservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeltaCheckServiceTest {

    @Mock
    private PatientLabObservationRepository observationRepository;

    @InjectMocks
    private DeltaCheckService deltaCheckService;

    @Test
    @DisplayName("Delta check within normal threshold: 100 -> 110 (10% shift) -> OK")
    void testDeltaCheckOk() {
        BigDecimal current = new BigDecimal("110.00");
        BigDecimal prev = new BigDecimal("100.00");

        DeltaCheckResult result = deltaCheckService.calculateDelta(current, prev, new BigDecimal("20.0"));

        assertNotNull(result);
        assertEquals(new BigDecimal("10.00"), result.getDeltaPercentage());
        assertEquals("OK", result.getFlag());
        assertFalse(result.isWarning());
    }

    @Test
    @DisplayName("Delta check exceeding threshold: 100 -> 135 (35% shift) -> DELTA_WARNING")
    void testDeltaCheckWarning() {
        BigDecimal current = new BigDecimal("135.00");
        BigDecimal prev = new BigDecimal("100.00");

        DeltaCheckResult result = deltaCheckService.calculateDelta(current, prev, new BigDecimal("20.0"));

        assertNotNull(result);
        assertEquals(new BigDecimal("35.00"), result.getDeltaPercentage());
        assertEquals("DELTA_WARNING", result.getFlag());
        assertTrue(result.isWarning());
    }

    @Test
    @DisplayName("Delta check downward shift exceeding threshold: 100 -> 60 (40% drop) -> DELTA_WARNING")
    void testDeltaCheckDownwardWarning() {
        BigDecimal current = new BigDecimal("60.00");
        BigDecimal prev = new BigDecimal("100.00");

        DeltaCheckResult result = deltaCheckService.calculateDelta(current, prev, new BigDecimal("20.0"));

        assertNotNull(result);
        assertEquals(new BigDecimal("40.00"), result.getDeltaPercentage());
        assertEquals("DELTA_WARNING", result.getFlag());
        assertTrue(result.isWarning());
    }
}
