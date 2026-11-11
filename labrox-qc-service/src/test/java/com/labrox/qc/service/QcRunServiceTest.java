package com.labrox.qc.service;

import com.labrox.qc.domain.QcAssayTarget;
import com.labrox.qc.domain.QcRunData;
import com.labrox.qc.dto.QcRunRequest;
import com.labrox.qc.dto.WestgardEvaluationResult;
import com.labrox.qc.repository.QcAssayTargetRepository;
import com.labrox.qc.repository.QcRunDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QcRunServiceTest {

    @Mock
    private QcAssayTargetRepository assayTargetRepository;

    @Mock
    private QcRunDataRepository runDataRepository;

    @Mock
    private WestgardEvaluatorService westgardEvaluator;

    @InjectMocks
    private QcRunService qcRunService;

    private QcAssayTarget sampleAssay;

    @BeforeEach
    void setUp() {
        sampleAssay = QcAssayTarget.builder()
                .id(10)
                .lotId(1)
                .observationId(101)
                .observationName("Glucose")
                .targetMean(new BigDecimal("100.00"))
                .targetSd(new BigDecimal("5.00"))
                .build();
    }

    @Test
    @DisplayName("Record in-control QC measurement: measured 102.5 -> Z=0.500")
    void testRecordInControl() {
        when(assayTargetRepository.findById(10)).thenReturn(Optional.of(sampleAssay));
        when(runDataRepository.findRecentRuns(eq(10), any())).thenReturn(Collections.emptyList());
        when(westgardEvaluator.evaluate(any(), any())).thenReturn(
                WestgardEvaluationResult.builder().status("IN_CONTROL").lockout(false).build()
        );
        when(runDataRepository.save(any(QcRunData.class))).thenAnswer(i -> i.getArgument(0));

        QcRunRequest req = QcRunRequest.builder()
                .assayTargetId(10)
                .analyzerId("COBAS-6000")
                .measuredValue(new BigDecimal("102.50"))
                .build();

        Optional<QcRunData> result = qcRunService.recordMeasurement(req);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("0.500"), result.get().getZScore());
        assertEquals("IN_CONTROL", result.get().getStatus());
        assertFalse(result.get().isLockoutTriggered());
    }

    @Test
    @DisplayName("Record out-of-control measurement triggers lockout: measured 120 -> Z=4.000 -> 1_3s REJECTED")
    void testRecordOutControlLockout() {
        when(assayTargetRepository.findById(10)).thenReturn(Optional.of(sampleAssay));
        when(runDataRepository.findRecentRuns(eq(10), any())).thenReturn(Collections.emptyList());
        when(westgardEvaluator.evaluate(any(), any())).thenReturn(
                WestgardEvaluationResult.builder()
                        .status("REJECTED")
                        .violatedRule("1_3s")
                        .lockout(true)
                        .description("Exceeds 3SD")
                        .build()
        );
        when(runDataRepository.save(any(QcRunData.class))).thenAnswer(i -> i.getArgument(0));

        QcRunRequest req = QcRunRequest.builder()
                .assayTargetId(10)
                .analyzerId("COBAS-6000")
                .measuredValue(new BigDecimal("120.00"))
                .build();

        Optional<QcRunData> result = qcRunService.recordMeasurement(req);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("4.000"), result.get().getZScore());
        assertEquals("REJECTED", result.get().getStatus());
        assertTrue(result.get().isLockoutTriggered(), "Analyzer lockout must be triggered on 1_3s violation");
    }
}
