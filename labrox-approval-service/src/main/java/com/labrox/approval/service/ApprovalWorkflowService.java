package com.labrox.approval.service;

import com.labrox.approval.domain.*;
import com.labrox.approval.dto.AmendmentRequest;
import com.labrox.approval.dto.ApprovalRequest;
import com.labrox.approval.dto.HoldRerunRequest;
import com.labrox.approval.dto.PanicCallRequest;
import com.labrox.approval.repository.AmendmentHistoryRepository;
import com.labrox.approval.repository.ElectronicSignatureRepository;
import com.labrox.approval.repository.PanicCallLogRepository;
import com.labrox.approval.repository.TestApprovalLogRepository;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.domain.PatientLabObservation;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import com.labrox.diagnostic.repository.PatientLabObservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalWorkflowService {

    private final TestApprovalLogRepository approvalLogRepository;
    private final ElectronicSignatureRepository signatureRepository;
    private final AmendmentHistoryRepository amendmentRepository;
    private final PanicCallLogRepository panicCallRepository;
    private final ElectronicSignatureService electronicSignatureService;
    private final PatientLabInvestigationRepository investigationRepository;
    private final PatientLabObservationRepository observationRepository;

    @Transactional
    public Optional<TestApprovalLog> approveInvestigation(ApprovalRequest request) {
        Integer companyId = request.getCompanyId() != null ? request.getCompanyId() : 1;
        Optional<PatientLabInvestigation> invOpt = investigationRepository.findByCompanyIdAndTestId(companyId, request.getTestId());

        if (invOpt.isEmpty()) {
            return Optional.empty();
        }

        PatientLabInvestigation inv = invOpt.get();
        String signatureHash = null;

        // Pathologist final approval requires 21 CFR Part 11 Electronic Signature stamp
        if ("PATHOLOGIST".equalsIgnoreCase(request.getApproverRole())) {
            String meaning = request.getSignatureMeaning() != null ? request.getSignatureMeaning() : "I have reviewed and approved these diagnostic test results.";
            ElectronicSignature sig = electronicSignatureService.signReport(
                    companyId,
                    inv.getTestId(),
                    request.getApproverId(),
                    request.getApproverName(),
                    request.getRegistrationNumber() != null ? request.getRegistrationNumber() : "MCI-DEFAULT",
                    request.getDesignation() != null ? request.getDesignation() : "Consultant Pathologist",
                    meaning
            );
            signatureHash = sig.getSignatureHash();
            inv.setStatus("TEST_APPROVED");
        } else if ("RESIDENT".equalsIgnoreCase(request.getApproverRole())) {
            inv.setStatus("PROVISIONAL_REVIEW");
        } else {
            inv.setStatus("TEST_APPROVED");
        }

        investigationRepository.save(inv);

        TestApprovalLog logEntry = TestApprovalLog.builder()
                .companyId(companyId)
                .testId(inv.getTestId())
                .action("FINAL_APPROVE".equalsIgnoreCase(request.getApproverRole()) ? "FINAL_APPROVE" : "APPROVE")
                .approverId(request.getApproverId())
                .approverName(request.getApproverName())
                .approverRole(request.getApproverRole())
                .signatureHash(signatureHash)
                .comments(request.getComments())
                .build();

        TestApprovalLog saved = approvalLogRepository.save(logEntry);
        log.info("Investigation {} approved by {} (Role: {}, Status: {})", inv.getTestId(), request.getApproverName(), request.getApproverRole(), inv.getStatus());
        return Optional.of(saved);
    }

    @Transactional
    public Optional<TestApprovalLog> holdInvestigation(HoldRerunRequest request) {
        Integer companyId = request.getCompanyId() != null ? request.getCompanyId() : 1;
        Optional<PatientLabInvestigation> invOpt = investigationRepository.findByCompanyIdAndTestId(companyId, request.getTestId());

        if (invOpt.isEmpty()) {
            return Optional.empty();
        }

        PatientLabInvestigation inv = invOpt.get();
        inv.setStatus("HOLD");
        investigationRepository.save(inv);

        TestApprovalLog logEntry = TestApprovalLog.builder()
                .companyId(companyId)
                .testId(inv.getTestId())
                .action("HOLD")
                .approverId(request.getUserId())
                .approverName(request.getUserName())
                .approverRole("PATHOLOGIST")
                .comments(request.getReason())
                .build();

        TestApprovalLog saved = approvalLogRepository.save(logEntry);
        log.warn("Investigation {} placed on HOLD: {}", inv.getTestId(), request.getReason());
        return Optional.of(saved);
    }

    @Transactional
    public Optional<TestApprovalLog> rerunInvestigation(HoldRerunRequest request) {
        Integer companyId = request.getCompanyId() != null ? request.getCompanyId() : 1;
        Optional<PatientLabInvestigation> invOpt = investigationRepository.findByCompanyIdAndTestId(companyId, request.getTestId());

        if (invOpt.isEmpty()) {
            return Optional.empty();
        }

        PatientLabInvestigation inv = invOpt.get();
        // Reset status to SAMPLE_RECEIVED so it returns to bench worklist
        inv.setStatus("SAMPLE_RECEIVED");
        investigationRepository.save(inv);

        TestApprovalLog logEntry = TestApprovalLog.builder()
                .companyId(companyId)
                .testId(inv.getTestId())
                .action("RERUN")
                .approverId(request.getUserId())
                .approverName(request.getUserName())
                .approverRole("PATHOLOGIST")
                .comments(request.getReason())
                .build();

        TestApprovalLog saved = approvalLogRepository.save(logEntry);
        log.info("Investigation {} ordered for RERUN: {}", inv.getTestId(), request.getReason());
        return Optional.of(saved);
    }

    @Transactional
    public Optional<AmendmentHistory> amendResult(AmendmentRequest request) {
        Integer companyId = request.getCompanyId() != null ? request.getCompanyId() : 1;
        Optional<PatientLabInvestigation> invOpt = investigationRepository.findByCompanyIdAndTestId(companyId, request.getTestId());

        if (invOpt.isEmpty()) {
            return Optional.empty();
        }

        List<PatientLabObservation> observations = observationRepository.findByInvestigationTestId(request.getTestId());
        Optional<PatientLabObservation> obsOpt = observations.stream()
                .filter(o -> o.getObservationId().equals(request.getObservationId()))
                .findFirst();

        if (obsOpt.isEmpty()) {
            return Optional.empty();
        }

        PatientLabObservation obs = obsOpt.get();
        String originalVal = obs.getResultValue();

        // 1. Invalidate prior electronic signature
        Optional<ElectronicSignature> priorSigOpt = signatureRepository.findByCompanyIdAndTestIdAndValidTrue(companyId, request.getTestId());
        Long priorSigId = null;
        if (priorSigOpt.isPresent()) {
            ElectronicSignature prior = priorSigOpt.get();
            prior.setValid(false);
            signatureRepository.save(prior);
            priorSigId = prior.getId();
        }

        // 2. Update observation with amended value
        obs.setResultValue(request.getNewValue());
        try {
            obs.setNumericValue(new BigDecimal(request.getNewValue().trim()));
        } catch (Exception ignored) {
        }
        observationRepository.save(obs);

        // 3. Record 21 CFR Part 11 Amendment History
        AmendmentHistory amendment = AmendmentHistory.builder()
                .companyId(companyId)
                .testId(request.getTestId())
                .observationId(request.getObservationId())
                .observationName(request.getObservationName())
                .originalValue(originalVal)
                .amendedValue(request.getNewValue())
                .reasonForChange(request.getReasonForChange())
                .amendedBy(request.getPathologistId())
                .amendedByName(request.getPathologistName())
                .amendedAt(Instant.now())
                .supersededSignatureId(priorSigId)
                .build();

        AmendmentHistory saved = amendmentRepository.save(amendment);

        // 4. Generate new amended electronic signature stamp
        electronicSignatureService.signReport(
                companyId,
                request.getTestId(),
                request.getPathologistId(),
                request.getPathologistName(),
                request.getRegistrationNumber() != null ? request.getRegistrationNumber() : "MCI-DEFAULT",
                request.getDesignation() != null ? request.getDesignation() : "Consultant Pathologist",
                "Amended report approved: " + request.getReasonForChange()
        );

        log.info("Regulated Amendment recorded for TestId={}, Obs={}, Old={}, New={}",
                request.getTestId(), request.getObservationName(), originalVal, request.getNewValue());

        return Optional.of(saved);
    }

    @Transactional
    public PanicCallLog logPanicCall(PanicCallRequest request) {
        PanicCallLog callLog = PanicCallLog.builder()
                .companyId(request.getCompanyId() != null ? request.getCompanyId() : 1)
                .testId(request.getTestId())
                .patientId(request.getPatientId())
                .observationName(request.getObservationName())
                .panicValue(request.getPanicValue())
                .calledPersonName(request.getCalledPersonName())
                .calledPersonDesignation(request.getCalledPersonDesignation())
                .departmentOrWard(request.getDepartmentOrWard())
                .callTime(Instant.now())
                .callerId(request.getCallerId())
                .callStatus(request.getCallStatus() != null ? request.getCallStatus() : "INFORMED")
                .remarks(request.getRemarks())
                .build();

        PanicCallLog saved = panicCallRepository.save(callLog);
        log.warn("Panic call escalation logged: Patient={}, Value={}, Called={}, Status={}",
                request.getPatientId(), request.getPanicValue(), request.getCalledPersonName(), saved.getCallStatus());
        return saved;
    }
}
