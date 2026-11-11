package com.labrox.qc.service;

import com.labrox.qc.domain.QcAssayTarget;
import com.labrox.qc.domain.QcRunData;
import com.labrox.qc.dto.LeveyJenningsDataPoint;
import com.labrox.qc.dto.QcRunRequest;
import com.labrox.qc.dto.WestgardEvaluationResult;
import com.labrox.qc.repository.QcAssayTargetRepository;
import com.labrox.qc.repository.QcRunDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QcRunService {

    private final QcAssayTargetRepository assayTargetRepository;
    private final QcRunDataRepository runDataRepository;
    private final WestgardEvaluatorService westgardEvaluator;

    @Transactional
    public Optional<QcRunData> recordMeasurement(QcRunRequest request) {
        Optional<QcAssayTarget> targetOpt = assayTargetRepository.findById(request.getAssayTargetId());
        if (targetOpt.isEmpty()) {
            return Optional.empty();
        }

        QcAssayTarget target = targetOpt.get();
        BigDecimal mean = target.getTargetMean();
        BigDecimal sd = target.getTargetSd();

        if (sd == null || sd.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Target SD cannot be zero or null");
        }

        // Calculate Z-Score = (Measured - Mean) / SD
        BigDecimal diff = request.getMeasuredValue().subtract(mean);
        BigDecimal zScore = diff.divide(sd, 3, RoundingMode.HALF_UP);

        // Fetch recent historical Z-scores (ordered by runTime DESC)
        List<QcRunData> recentRuns = runDataRepository.findRecentRuns(target.getId(), PageRequest.of(0, 15));
        List<BigDecimal> historicalZ = recentRuns.stream()
                .map(QcRunData::getZScore)
                .collect(Collectors.toList());

        // Evaluate Westgard multi-rules
        WestgardEvaluationResult evaluation = westgardEvaluator.evaluate(zScore, historicalZ);

        QcRunData run = QcRunData.builder()
                .assayTargetId(target.getId())
                .runTime(Instant.now())
                .analyzerId(request.getAnalyzerId())
                .measuredValue(request.getMeasuredValue())
                .zScore(zScore)
                .status(evaluation.getStatus())
                .violatedRule(evaluation.getViolatedRule())
                .lockoutTriggered(evaluation.isLockout())
                .correctiveAction(request.getCorrectiveAction())
                .build();

        QcRunData saved = runDataRepository.save(run);
        if (evaluation.isLockout()) {
            log.error("ANALYZER LOCKOUT TRIGGERED: Analyzer={}, Target={}, Rule={}, Z={}",
                    run.getAnalyzerId(), target.getObservationName(), evaluation.getViolatedRule(), zScore);
        } else {
            log.info("QC Run recorded: Target={}, Value={}, Z={}, Status={}",
                    target.getObservationName(), request.getMeasuredValue(), zScore, evaluation.getStatus());
        }

        return Optional.of(saved);
    }

    public List<LeveyJenningsDataPoint> getLeveyJenningsPoints(Integer assayTargetId) {
        List<QcRunData> runs = runDataRepository.findByAssayTargetIdOrderByRunTimeAsc(assayTargetId);
        return runs.stream().map(r -> LeveyJenningsDataPoint.builder()
                .runId(r.getId())
                .runTime(r.getRunTime())
                .analyzerId(r.getAnalyzerId())
                .measuredValue(r.getMeasuredValue())
                .zScore(r.getZScore())
                .status(r.getStatus())
                .violatedRule(r.getViolatedRule())
                .lockoutTriggered(r.isLockoutTriggered())
                .build()
        ).collect(Collectors.toList());
    }
}
