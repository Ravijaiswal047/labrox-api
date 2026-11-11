package com.labrox.approval.service;

import com.labrox.approval.domain.AutoApprovalRule;
import com.labrox.approval.dto.AutoApprovalDecision;
import com.labrox.approval.repository.AutoApprovalRuleRepository;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.domain.PatientLabObservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoApprovalServiceTest {

    @Mock
    private AutoApprovalRuleRepository ruleRepository;

    @InjectMocks
    private AutoApprovalService autoApprovalService;

    private PatientLabInvestigation normalInv;
    private AutoApprovalRule activeRule;

    @BeforeEach
    void setUp() {
        normalInv = PatientLabInvestigation.builder()
                .testId(6001L)
                .companyId(1)
                .departmentId(2)
                .investigationId(10)
                .critical(false)
                .build();

        activeRule = AutoApprovalRule.builder()
                .id(1)
                .companyId(1)
                .investigationId(10)
                .enabled(true)
                .allowDeltaWarning(false)
                .allowPanicAlarm(false)
                .build();
    }

    @Test
    @DisplayName("Auto-approval passes when all observations are normal and no delta warning")
    void testAutoApprovalEligible() {
        when(ruleRepository.findByCompanyIdAndInvestigationIdAndEnabledTrue(1, 10)).thenReturn(Optional.of(activeRule));

        List<PatientLabObservation> observations = List.of(
                PatientLabObservation.builder().observationName("Glucose").flag("NORMAL").deltaFlag("OK").build(),
                PatientLabObservation.builder().observationName("Urea").flag("NORMAL").deltaFlag("OK").build()
        );

        AutoApprovalDecision decision = autoApprovalService.evaluateAutoApproval(normalInv, observations);

        assertNotNull(decision);
        assertTrue(decision.isEligible());
    }

    @Test
    @DisplayName("Auto-approval rejected when investigation has critical panic alarm")
    void testAutoApprovalRejectedOnPanic() {
        normalInv.setCritical(true);
        when(ruleRepository.findByCompanyIdAndInvestigationIdAndEnabledTrue(1, 10)).thenReturn(Optional.of(activeRule));

        List<PatientLabObservation> observations = List.of(
                PatientLabObservation.builder().observationName("Glucose").flag("CRITICAL_HIGH").deltaFlag("OK").build()
        );

        AutoApprovalDecision decision = autoApprovalService.evaluateAutoApproval(normalInv, observations);

        assertNotNull(decision);
        assertFalse(decision.isEligible());
        assertTrue(decision.getReason().contains("Critical Panic"));
    }

    @Test
    @DisplayName("Auto-approval rejected when delta check warning is present")
    void testAutoApprovalRejectedOnDelta() {
        when(ruleRepository.findByCompanyIdAndInvestigationIdAndEnabledTrue(1, 10)).thenReturn(Optional.of(activeRule));

        List<PatientLabObservation> observations = List.of(
                PatientLabObservation.builder().observationName("Hemoglobin").flag("NORMAL").deltaFlag("DELTA_WARNING").build()
        );

        AutoApprovalDecision decision = autoApprovalService.evaluateAutoApproval(normalInv, observations);

        assertNotNull(decision);
        assertFalse(decision.isEligible());
        assertTrue(decision.getReason().contains("Delta Check warning"));
    }

    @Test
    @DisplayName("Auto-approval rejected when observation reading is abnormal (High/Low)")
    void testAutoApprovalRejectedOnAbnormal() {
        when(ruleRepository.findByCompanyIdAndInvestigationIdAndEnabledTrue(1, 10)).thenReturn(Optional.of(activeRule));

        List<PatientLabObservation> observations = List.of(
                PatientLabObservation.builder().observationName("Platelets").flag("LOW").deltaFlag("OK").build()
        );

        AutoApprovalDecision decision = autoApprovalService.evaluateAutoApproval(normalInv, observations);

        assertNotNull(decision);
        assertFalse(decision.isEligible());
        assertTrue(decision.getReason().contains("outside normal reference interval"));
    }
}
