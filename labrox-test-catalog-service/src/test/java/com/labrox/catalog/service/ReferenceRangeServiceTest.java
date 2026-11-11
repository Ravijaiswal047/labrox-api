package com.labrox.catalog.service;

import com.labrox.catalog.domain.LabObservationRange;
import com.labrox.catalog.dto.ReferenceRangeEvaluationResult;
import com.labrox.catalog.repository.LabObservationRangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceRangeServiceTest {

    @Mock
    private LabObservationRangeRepository rangeRepository;

    @InjectMocks
    private ReferenceRangeService rangeService;

    private LabObservationRange adultHemoglobinRange;

    @BeforeEach
    void setUp() {
        adultHemoglobinRange = LabObservationRange.builder()
                .id(1)
                .observationId(101)
                .gender("Male")
                .fromAgeDays(6570)  // 18 years
                .toAgeDays(43800) // 120 years
                .minReading(new BigDecimal("13.00"))
                .maxReading(new BigDecimal("17.00"))
                .minCritical(new BigDecimal("7.00"))
                .maxCritical(new BigDecimal("20.00"))
                .interpretation("Normal adult male reference range")
                .build();
    }

    @Test
    @DisplayName("Evaluate Hemoglobin: 14.50 g/dL -> NORMAL")
    void testNormalReading() {
        when(rangeRepository.findMatchingRanges(101, "Male", 10000))
                .thenReturn(List.of(adultHemoglobinRange));

        ReferenceRangeEvaluationResult result = rangeService.evaluateResult(
                101, "Male", 10000, new BigDecimal("14.50")
        );

        assertNotNull(result);
        assertEquals("NORMAL", result.getFlag());
        assertEquals("13 - 17", result.getDisplayRange());
    }

    @Test
    @DisplayName("Evaluate Hemoglobin: 11.50 g/dL -> LOW")
    void testLowReading() {
        when(rangeRepository.findMatchingRanges(101, "Male", 10000))
                .thenReturn(List.of(adultHemoglobinRange));

        ReferenceRangeEvaluationResult result = rangeService.evaluateResult(
                101, "Male", 10000, new BigDecimal("11.50")
        );

        assertNotNull(result);
        assertEquals("LOW", result.getFlag());
    }

    @Test
    @DisplayName("Evaluate Hemoglobin: 18.50 g/dL -> HIGH")
    void testHighReading() {
        when(rangeRepository.findMatchingRanges(101, "Male", 10000))
                .thenReturn(List.of(adultHemoglobinRange));

        ReferenceRangeEvaluationResult result = rangeService.evaluateResult(
                101, "Male", 10000, new BigDecimal("18.50")
        );

        assertNotNull(result);
        assertEquals("HIGH", result.getFlag());
    }

    @Test
    @DisplayName("Evaluate Hemoglobin: 6.20 g/dL -> CRITICAL_LOW (Panic Alarm)")
    void testCriticalPanicLow() {
        when(rangeRepository.findMatchingRanges(101, "Male", 10000))
                .thenReturn(List.of(adultHemoglobinRange));

        ReferenceRangeEvaluationResult result = rangeService.evaluateResult(
                101, "Male", 10000, new BigDecimal("6.20")
        );

        assertNotNull(result);
        assertEquals("CRITICAL_LOW", result.getFlag());
    }

    @Test
    @DisplayName("Evaluate Hemoglobin: 21.00 g/dL -> CRITICAL_HIGH (Panic Alarm)")
    void testCriticalPanicHigh() {
        when(rangeRepository.findMatchingRanges(101, "Male", 10000))
                .thenReturn(List.of(adultHemoglobinRange));

        ReferenceRangeEvaluationResult result = rangeService.evaluateResult(
                101, "Male", 10000, new BigDecimal("21.00")
        );

        assertNotNull(result);
        assertEquals("CRITICAL_HIGH", result.getFlag());
    }
}
