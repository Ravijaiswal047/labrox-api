package com.labrox.approval.service;

import com.labrox.approval.domain.*;
import com.labrox.approval.dto.*;
import com.labrox.approval.repository.*;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.domain.PatientLabObservation;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import com.labrox.diagnostic.repository.PatientLabObservationRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalWorkflowServiceTest {

    @Mock
    private TestApprovalLogRepository approvalLogRepository;

    @Mock
    private ElectronicSignatureRepository signatureRepository;

    @Mock
    private AmendmentHistoryRepository amendmentRepository;

    @Mock
    private PanicCallLogRepository panicCallRepository;

    @Mock
    private ElectronicSignatureService electronicSignatureService;

    @Mock
    private PatientLabInvestigationRepository investigationRepository;

    @Mock
    private PatientLabObservationRepository observationRepository;

    @InjectMocks
    private ApprovalWorkflowService workflowService;

    private PatientLabInvestigation sampleInv;

    @BeforeEach
    void setUp() {
        sampleInv = PatientLabInvestigation.builder()
                .testId(7001L)
                .companyId(1)
                .status("RESULT_ENTERED")
                .build();
    }

    @Test
    @DisplayName("Pathologist approval stamps digital signature and sets status TEST_APPROVED")
    void testPathologistApproval() {
        when(investigationRepository.findByCompanyIdAndTestId(1, 7001L)).thenReturn(Optional.of(sampleInv));
        when(electronicSignatureService.signReport(any(), any(), any(), any(), any(), any(), any())).thenReturn(
                ElectronicSignature.builder().signatureHash("abc123hash").build()
        );
        when(approvalLogRepository.save(any(TestApprovalLog.class))).thenAnswer(i -> i.getArgument(0));

        ApprovalRequest req = ApprovalRequest.builder()
                .companyId(1)
                .testId(7001L)
                .approverId(15)
                .approverName("Dr. Anita Roy")
                .approverRole("PATHOLOGIST")
                .registrationNumber("MCI-12345")
                .designation("Senior Consultant")
                .build();

        Optional<TestApprovalLog> logResult = workflowService.approveInvestigation(req);

        assertTrue(logResult.isPresent());
        assertEquals("TEST_APPROVED", sampleInv.getStatus());
        assertEquals("abc123hash", logResult.get().getSignatureHash());
        verify(investigationRepository).save(sampleInv);
    }

    @Test
    @DisplayName("Hold investigation updates status to HOLD")
    void testHoldInvestigation() {
        when(investigationRepository.findByCompanyIdAndTestId(1, 7001L)).thenReturn(Optional.of(sampleInv));
        when(approvalLogRepository.save(any(TestApprovalLog.class))).thenAnswer(i -> i.getArgument(0));

        HoldRerunRequest req = HoldRerunRequest.builder()
                .companyId(1)
                .testId(7001L)
                .userId(15)
                .userName("Dr. Anita Roy")
                .action("HOLD")
                .reason("Requires clinical correlation with treating physician")
                .build();

        Optional<TestApprovalLog> logResult = workflowService.holdInvestigation(req);

        assertTrue(logResult.isPresent());
        assertEquals("HOLD", sampleInv.getStatus());
        assertEquals("HOLD", logResult.get().getAction());
    }

    @Test
    @DisplayName("Rerun investigation resets status to SAMPLE_RECEIVED for re-testing")
    void testRerunInvestigation() {
        when(investigationRepository.findByCompanyIdAndTestId(1, 7001L)).thenReturn(Optional.of(sampleInv));
        when(approvalLogRepository.save(any(TestApprovalLog.class))).thenAnswer(i -> i.getArgument(0));

        HoldRerunRequest req = HoldRerunRequest.builder()
                .companyId(1)
                .testId(7001L)
                .userId(15)
                .userName("Dr. Anita Roy")
                .action("RERUN")
                .reason("Severe unexpected delta shift observed")
                .build();

        Optional<TestApprovalLog> logResult = workflowService.rerunInvestigation(req);

        assertTrue(logResult.isPresent());
        assertEquals("SAMPLE_RECEIVED", sampleInv.getStatus());
        assertEquals("RERUN", logResult.get().getAction());
    }

    @Test
    @DisplayName("Regulated Amendment invalidates prior signature and persists amendment diff")
    void testAmendResult() {
        PatientLabObservation obs = PatientLabObservation.builder()
                .observationId(101)
                .observationName("Hemoglobin")
                .resultValue("8.5")
                .build();

        ElectronicSignature priorSig = ElectronicSignature.builder()
                .id(99L)
                .signatureHash("oldSigHash")
                .valid(true)
                .build();

        when(investigationRepository.findByCompanyIdAndTestId(1, 7001L)).thenReturn(Optional.of(sampleInv));
        when(observationRepository.findByInvestigationTestId(7001L)).thenReturn(List.of(obs));
        when(signatureRepository.findByCompanyIdAndTestIdAndValidTrue(1, 7001L)).thenReturn(Optional.of(priorSig));
        when(amendmentRepository.save(any(AmendmentHistory.class))).thenAnswer(i -> i.getArgument(0));

        AmendmentRequest req = AmendmentRequest.builder()
                .companyId(1)
                .testId(7001L)
                .observationId(101)
                .observationName("Hemoglobin")
                .newValue("9.2")
                .reasonForChange("Typographical correction from analyzer log review")
                .pathologistId(15)
                .pathologistName("Dr. Anita Roy")
                .build();

        Optional<AmendmentHistory> result = workflowService.amendResult(req);

        assertTrue(result.isPresent());
        assertFalse(priorSig.isValid(), "Prior signature must be invalidated");
        assertEquals("8.5", result.get().getOriginalValue());
        assertEquals("9.2", result.get().getAmendedValue());
        assertEquals("9.2", obs.getResultValue());
        verify(signatureRepository).save(priorSig);
        verify(observationRepository).save(obs);
    }
}
