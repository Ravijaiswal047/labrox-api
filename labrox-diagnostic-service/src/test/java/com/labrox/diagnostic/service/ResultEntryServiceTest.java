package com.labrox.diagnostic.service;

import com.labrox.catalog.dto.ReferenceRangeEvaluationResult;
import com.labrox.catalog.service.ReferenceRangeService;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.dto.DeltaCheckResult;
import com.labrox.diagnostic.dto.ObservationEntryDto;
import com.labrox.diagnostic.dto.SaveResultEntryRequest;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import com.labrox.diagnostic.repository.PatientLabObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultEntryServiceTest {

    @Mock
    private PatientLabInvestigationRepository investigationRepository;

    @Mock
    private PatientLabObservationRepository observationRepository;

    @Mock
    private ReferenceRangeService referenceRangeService;

    @Mock
    private DeltaCheckService deltaCheckService;

    @InjectMocks
    private ResultEntryService resultEntryService;

    private PatientLabInvestigation sampleInvestigation;

    @BeforeEach
    void setUp() {
        sampleInvestigation = PatientLabInvestigation.builder()
                .testId(1001L)
                .companyId(1)
                .barcodeNo("S26307123456")
                .orderId("ORD-550")
                .patientId(200)
                .patientName("Jane Doe")
                .patientAgeDays(12000)
                .patientGender("Female")
                .investigationId(5)
                .testCode("L1001")
                .testName("Complete Blood Count")
                .departmentId(1)
                .status("SAMPLE_RECEIVED")
                .critical(false)
                .build();
    }

    @Test
    @DisplayName("Save normal results updates status to RESULT_ENTERED with isCritical=false")
    void testSaveNormalResults() {
        when(investigationRepository.findByCompanyIdAndTestId(1, 1001L)).thenReturn(Optional.of(sampleInvestigation));
        when(referenceRangeService.evaluateResult(any(), any(), any(), any())).thenReturn(
                ReferenceRangeEvaluationResult.builder().flag("NORMAL").displayRange("12.0 - 15.5").build()
        );
        when(deltaCheckService.evaluateDelta(any(), any(), any(), any())).thenReturn(
                DeltaCheckResult.builder().flag("OK").warning(false).build()
        );
        when(investigationRepository.save(any(PatientLabInvestigation.class))).thenAnswer(i -> i.getArgument(0));

        SaveResultEntryRequest req = SaveResultEntryRequest.builder()
                .companyId(1)
                .testId(1001L)
                .technicianId(50)
                .observations(List.of(
                        ObservationEntryDto.builder()
                                .observationId(101)
                                .observationName("Hemoglobin")
                                .resultValue("13.5")
                                .numericValue(new BigDecimal("13.5"))
                                .unit("g/dL")
                                .build()
                ))
                .build();

        Optional<PatientLabInvestigation> result = resultEntryService.saveResults(req);

        assertTrue(result.isPresent());
        assertEquals("RESULT_ENTERED", result.get().getStatus());
        assertFalse(result.get().isCritical());
    }

    @Test
    @DisplayName("Save critical panic result triggers isCritical=true on investigation")
    void testSaveCriticalPanicResults() {
        when(investigationRepository.findByCompanyIdAndTestId(1, 1001L)).thenReturn(Optional.of(sampleInvestigation));
        when(referenceRangeService.evaluateResult(any(), any(), any(), any())).thenReturn(
                ReferenceRangeEvaluationResult.builder().flag("CRITICAL_LOW").displayRange("12.0 - 15.5").build()
        );
        when(deltaCheckService.evaluateDelta(any(), any(), any(), any())).thenReturn(
                DeltaCheckResult.builder().flag("OK").warning(false).build()
        );
        when(investigationRepository.save(any(PatientLabInvestigation.class))).thenAnswer(i -> i.getArgument(0));

        SaveResultEntryRequest req = SaveResultEntryRequest.builder()
                .companyId(1)
                .testId(1001L)
                .technicianId(50)
                .observations(List.of(
                        ObservationEntryDto.builder()
                                .observationId(101)
                                .observationName("Hemoglobin")
                                .resultValue("5.8")
                                .numericValue(new BigDecimal("5.8"))
                                .unit("g/dL")
                                .build()
                ))
                .build();

        Optional<PatientLabInvestigation> result = resultEntryService.saveResults(req);

        assertTrue(result.isPresent());
        assertEquals("RESULT_ENTERED", result.get().getStatus());
        assertTrue(result.get().isCritical(), "Critical panic alarm should be set to true");
    }
}
