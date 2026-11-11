package com.labrox.diagnostic.service;

import com.labrox.catalog.dto.ReferenceRangeEvaluationResult;
import com.labrox.catalog.service.ReferenceRangeService;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.domain.PatientLabObservation;
import com.labrox.diagnostic.dto.DeltaCheckResult;
import com.labrox.diagnostic.dto.ObservationEntryDto;
import com.labrox.diagnostic.dto.SaveResultEntryRequest;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import com.labrox.diagnostic.repository.PatientLabObservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultEntryService {

    private final PatientLabInvestigationRepository investigationRepository;
    private final PatientLabObservationRepository observationRepository;
    private final ReferenceRangeService referenceRangeService;
    private final DeltaCheckService deltaCheckService;

    @Transactional
    public Optional<PatientLabInvestigation> saveResults(SaveResultEntryRequest request) {
        Integer companyId = request.getCompanyId() != null ? request.getCompanyId() : 1;
        Optional<PatientLabInvestigation> invOpt = investigationRepository.findByCompanyIdAndTestId(companyId, request.getTestId());

        if (invOpt.isEmpty()) {
            return Optional.empty();
        }

        PatientLabInvestigation inv = invOpt.get();
        boolean hasCritical = false;

        List<PatientLabObservation> savedObs = new ArrayList<>();

        for (ObservationEntryDto entry : request.getObservations()) {
            BigDecimal numVal = entry.getNumericValue();
            if (numVal == null && entry.getResultValue() != null) {
                try {
                    numVal = new BigDecimal(entry.getResultValue().trim());
                } catch (NumberFormatException ignored) {
                }
            }

            // Reference interval & panic check
            ReferenceRangeEvaluationResult rangeEval = referenceRangeService.evaluateResult(
                    entry.getObservationId(),
                    inv.getPatientGender(),
                    inv.getPatientAgeDays(),
                    numVal
            );

            // Delta check against historical values
            DeltaCheckResult deltaEval = deltaCheckService.evaluateDelta(
                    companyId,
                    inv.getPatientId(),
                    entry.getObservationId(),
                    numVal
            );

            String flag = rangeEval.getFlag();
            if ("CRITICAL_LOW".equals(flag) || "CRITICAL_HIGH".equals(flag)) {
                hasCritical = true;
                log.warn("CRITICAL PANIC ALARM: TestId={}, Obs={}, Value={}, Flag={}",
                        inv.getTestId(), entry.getObservationName(), numVal, flag);
            }

            PatientLabObservation obs = PatientLabObservation.builder()
                    .investigation(inv)
                    .observationId(entry.getObservationId())
                    .observationName(entry.getObservationName())
                    .resultValue(entry.getResultValue())
                    .numericValue(numVal)
                    .oldValue(deltaEval.getPreviousValue())
                    .deltaPercentage(deltaEval.getDeltaPercentage())
                    .deltaFlag(deltaEval.getFlag())
                    .unit(entry.getUnit())
                    .minReading(rangeEval.getMinReading())
                    .maxReading(rangeEval.getMaxReading())
                    .minCritical(rangeEval.getMinCritical())
                    .maxCritical(rangeEval.getMaxCritical())
                    .displayReading(rangeEval.getDisplayRange())
                    .flag(flag)
                    .comment(entry.getComment())
                    .build();

            savedObs.add(obs);
        }

        observationRepository.saveAll(savedObs);

        inv.setStatus("RESULT_ENTERED");
        inv.setCritical(hasCritical);
        inv.setResultDate(Instant.now());
        inv.setResultEnteredBy(request.getTechnicianId());

        PatientLabInvestigation saved = investigationRepository.save(inv);
        log.info("Results entered successfully: TestId={}, Critical={}", saved.getTestId(), saved.isCritical());
        return Optional.of(saved);
    }

    public Optional<PatientLabInvestigation> getTestDetails(Integer companyId, Long testId) {
        return investigationRepository.findWithObservations(companyId != null ? companyId : 1, testId);
    }

    public List<PatientLabInvestigation> getWorklist(Integer companyId, String status) {
        return investigationRepository.findByCompanyIdAndStatus(
                companyId != null ? companyId : 1,
                status != null ? status : "SAMPLE_RECEIVED"
        );
    }
}
