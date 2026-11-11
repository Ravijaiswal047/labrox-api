package com.labrox.catalog.service;

import com.labrox.catalog.domain.LabObservationRange;
import com.labrox.catalog.dto.ReferenceRangeEvaluationResult;
import com.labrox.catalog.repository.LabObservationRangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferenceRangeService {

    private final LabObservationRangeRepository rangeRepository;

    public Optional<LabObservationRange> findApplicableRange(Integer observationId, String gender, Integer ageDays) {
        if (observationId == null || gender == null || ageDays == null) {
            return Optional.empty();
        }

        List<LabObservationRange> matches = rangeRepository.findMatchingRanges(observationId, gender, ageDays);
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    public ReferenceRangeEvaluationResult evaluateResult(Integer observationId, String gender, Integer ageDays, BigDecimal value) {
        Optional<LabObservationRange> rangeOpt = findApplicableRange(observationId, gender, ageDays);

        if (rangeOpt.isEmpty()) {
            return ReferenceRangeEvaluationResult.builder()
                    .observationId(observationId)
                    .value(value)
                    .flag("NORMAL")
                    .displayRange("N/A")
                    .build();
        }

        LabObservationRange r = rangeOpt.get();
        String displayRange = (r.getMinReading() != null && r.getMaxReading() != null)
                ? r.getMinReading().stripTrailingZeros().toPlainString() + " - " + r.getMaxReading().stripTrailingZeros().toPlainString()
                : "N/A";

        if (value == null) {
            return ReferenceRangeEvaluationResult.builder()
                    .observationId(observationId)
                    .displayRange(displayRange)
                    .minReading(r.getMinReading())
                    .maxReading(r.getMaxReading())
                    .minCritical(r.getMinCritical())
                    .maxCritical(r.getMaxCritical())
                    .interpretation(r.getInterpretation())
                    .flag("NORMAL")
                    .build();
        }

        String flag = "NORMAL";

        // Check Critical Panic High
        if (r.getMaxCritical() != null && value.compareTo(r.getMaxCritical()) > 0) {
            flag = "CRITICAL_HIGH";
        }
        // Check Critical Panic Low
        else if (r.getMinCritical() != null && value.compareTo(r.getMinCritical()) < 0) {
            flag = "CRITICAL_LOW";
        }
        // Check High
        else if (r.getMaxReading() != null && value.compareTo(r.getMaxReading()) > 0) {
            flag = "HIGH";
        }
        // Check Low
        else if (r.getMinReading() != null && value.compareTo(r.getMinReading()) < 0) {
            flag = "LOW";
        }

        return ReferenceRangeEvaluationResult.builder()
                .observationId(observationId)
                .value(value)
                .flag(flag)
                .displayRange(displayRange)
                .minReading(r.getMinReading())
                .maxReading(r.getMaxReading())
                .minCritical(r.getMinCritical())
                .maxCritical(r.getMaxCritical())
                .interpretation(r.getInterpretation())
                .build();
    }
}
