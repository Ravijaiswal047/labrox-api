package com.labrox.diagnostic.service;

import com.labrox.diagnostic.domain.PatientLabObservation;
import com.labrox.diagnostic.dto.DeltaCheckResult;
import com.labrox.diagnostic.repository.PatientLabObservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeltaCheckService {

    private final PatientLabObservationRepository observationRepository;
    private static final BigDecimal DEFAULT_DELTA_THRESHOLD_PERCENT = new BigDecimal("20.0");

    public DeltaCheckResult evaluateDelta(Integer companyId, Integer patientId, Integer observationId, BigDecimal currentValue) {
        if (currentValue == null || patientId == null || observationId == null) {
            return DeltaCheckResult.builder().currentValue(currentValue).flag("OK").warning(false).build();
        }

        List<PatientLabObservation> history = observationRepository.findHistoricalReadings(companyId, patientId, observationId);
        if (history.isEmpty() || history.get(0).getNumericValue() == null) {
            return DeltaCheckResult.builder().currentValue(currentValue).flag("OK").warning(false).build();
        }

        BigDecimal prevValue = history.get(0).getNumericValue();
        return calculateDelta(currentValue, prevValue, DEFAULT_DELTA_THRESHOLD_PERCENT);
    }

    public DeltaCheckResult calculateDelta(BigDecimal currentValue, BigDecimal prevValue, BigDecimal thresholdPercent) {
        if (currentValue == null || prevValue == null || prevValue.compareTo(BigDecimal.ZERO) == 0) {
            return DeltaCheckResult.builder()
                    .currentValue(currentValue)
                    .previousValue(prevValue)
                    .flag("OK")
                    .warning(false)
                    .build();
        }

        BigDecimal diff = currentValue.subtract(prevValue).abs();
        BigDecimal percentage = diff.divide(prevValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        boolean isWarning = percentage.compareTo(thresholdPercent) > 0;
        String flag = isWarning ? "DELTA_WARNING" : "OK";

        if (isWarning) {
            log.warn("Delta Check Warning: Current={}, Previous={}, Shift={}%", currentValue, prevValue, percentage);
        }

        return DeltaCheckResult.builder()
                .currentValue(currentValue)
                .previousValue(prevValue)
                .deltaPercentage(percentage)
                .flag(flag)
                .warning(isWarning)
                .build();
    }
}
