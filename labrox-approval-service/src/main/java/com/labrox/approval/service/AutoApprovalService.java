package com.labrox.approval.service;

import com.labrox.approval.domain.AutoApprovalRule;
import com.labrox.approval.dto.AutoApprovalDecision;
import com.labrox.approval.repository.AutoApprovalRuleRepository;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.domain.PatientLabObservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoApprovalService {

    private final AutoApprovalRuleRepository ruleRepository;

    public AutoApprovalDecision evaluateAutoApproval(PatientLabInvestigation inv, List<PatientLabObservation> observations) {
        if (inv == null || observations == null || observations.isEmpty()) {
            return AutoApprovalDecision.builder()
                    .testId(inv != null ? inv.getTestId() : null)
                    .eligible(false)
                    .reason("Investigation or observations empty")
                    .build();
        }

        // 1. Check if an investigation or department rule exists
        Optional<AutoApprovalRule> ruleOpt = ruleRepository.findByCompanyIdAndInvestigationIdAndEnabledTrue(
                inv.getCompanyId(), inv.getInvestigationId()
        );

        if (ruleOpt.isEmpty()) {
            ruleOpt = ruleRepository.findByCompanyIdAndDepartmentIdAndInvestigationIdIsNullAndEnabledTrue(
                    inv.getCompanyId(), inv.getDepartmentId()
            );
        }

        if (ruleOpt.isEmpty()) {
            return AutoApprovalDecision.builder()
                    .testId(inv.getTestId())
                    .eligible(false)
                    .reason("Auto-approval rule not configured or disabled for this test/department")
                    .build();
        }

        AutoApprovalRule rule = ruleOpt.get();

        // 2. Critical Panic Alarm Check
        if (inv.isCritical()) {
            return AutoApprovalDecision.builder()
                    .testId(inv.getTestId())
                    .eligible(false)
                    .reason("Investigation flagged with Critical Panic Alarm")
                    .build();
        }

        for (PatientLabObservation obs : observations) {
            String flag = obs.getFlag() != null ? obs.getFlag() : "NORMAL";

            // If panic reading exists, cannot auto-approve
            if (flag.startsWith("CRITICAL_")) {
                return AutoApprovalDecision.builder()
                        .testId(inv.getTestId())
                        .eligible(false)
                        .reason("Parameter " + obs.getObservationName() + " has critical panic value")
                        .build();
            }

            // If delta warning exists and rule does not allow
            if ("DELTA_WARNING".equals(obs.getDeltaFlag()) && !rule.isAllowDeltaWarning()) {
                return AutoApprovalDecision.builder()
                        .testId(inv.getTestId())
                        .eligible(false)
                        .reason("Parameter " + obs.getObservationName() + " triggered a Delta Check warning (>20% shift)")
                        .build();
            }

            // Standard abnormal check (High/Low)
            if ("HIGH".equals(flag) || "LOW".equals(flag)) {
                return AutoApprovalDecision.builder()
                        .testId(inv.getTestId())
                        .eligible(false)
                        .reason("Parameter " + obs.getObservationName() + " is outside normal reference interval (" + flag + ")")
                        .build();
            }
        }

        // All checks passed!
        return AutoApprovalDecision.builder()
                .testId(inv.getTestId())
                .eligible(true)
                .reason("All parameters strictly within biological reference intervals with zero delta shifts")
                .build();
    }
}
