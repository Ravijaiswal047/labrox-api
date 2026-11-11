package com.labrox.qc.service;

import com.labrox.qc.dto.WestgardEvaluationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class WestgardEvaluatorService {

    /**
     * Evaluates a new measurement against historical Z-scores using Westgard Multi-rules:
     * - 1_3s: Random error -> REJECT & LOCKOUT
     * - 2_2s: Systematic error -> REJECT & LOCKOUT
     * - R_4s: Random error -> REJECT & LOCKOUT
     * - 4_1s: Systematic trend -> REJECT
     * - 10_x: Systematic bias -> REJECT
     * - 1_2s: Warning rule -> WARNING
     *
     * @param currentZ Current run Z-score
     * @param historicalZ List of preceding Z-scores, ordered from most recent to oldest (index 0 is previous run)
     */
    public WestgardEvaluationResult evaluate(BigDecimal currentZ, List<BigDecimal> historicalZ) {
        if (currentZ == null) {
            return WestgardEvaluationResult.builder().status("IN_CONTROL").lockout(false).build();
        }

        double z0 = currentZ.doubleValue();

        // Rule 1_3s: Single control > 3s (Random error)
        if (Math.abs(z0) > 3.0) {
            return WestgardEvaluationResult.builder()
                    .zScore(currentZ)
                    .status("REJECTED")
                    .violatedRule("1_3s")
                    .lockout(true)
                    .description("Rule 1-3s Violated: Measurement exceeds 3 standard deviations (Random Error). Analyzer locked out.")
                    .build();
        }

        // Rule 2_2s: Two consecutive controls > 2s on same side of mean (Systematic error)
        if (historicalZ != null && !historicalZ.isEmpty()) {
            double z1 = historicalZ.get(0).doubleValue();
            if ((z0 > 2.0 && z1 > 2.0) || (z0 < -2.0 && z1 < -2.0)) {
                return WestgardEvaluationResult.builder()
                        .zScore(currentZ)
                        .status("REJECTED")
                        .violatedRule("2_2s")
                        .lockout(true)
                        .description("Rule 2-2s Violated: Two consecutive controls exceed 2s on same side (Systematic Error).")
                        .build();
            }

            // Rule R_4s: Range across two consecutive controls >= 4s (e.g. +2.1 and -2.1)
            if (Math.abs(z0 - z1) >= 4.0) {
                return WestgardEvaluationResult.builder()
                        .zScore(currentZ)
                        .status("REJECTED")
                        .violatedRule("R_4s")
                        .lockout(true)
                        .description("Rule R-4s Violated: Difference between consecutive controls is >= 4 standard deviations.")
                        .build();
            }
        }

        // Rule 4_1s: Four consecutive controls > 1s on same side of mean (Systematic shift)
        if (historicalZ != null && historicalZ.size() >= 3) {
            double z1 = historicalZ.get(0).doubleValue();
            double z2 = historicalZ.get(1).doubleValue();
            double z3 = historicalZ.get(2).doubleValue();

            if ((z0 > 1.0 && z1 > 1.0 && z2 > 1.0 && z3 > 1.0) ||
                (z0 < -1.0 && z1 < -1.0 && z2 < -1.0 && z3 < -1.0)) {
                return WestgardEvaluationResult.builder()
                        .zScore(currentZ)
                        .status("REJECTED")
                        .violatedRule("4_1s")
                        .lockout(false)
                        .description("Rule 4-1s Violated: Four consecutive controls exceed 1s on same side.")
                        .build();
            }
        }

        // Rule 10_x: Ten consecutive controls on same side of mean (Systematic bias)
        if (historicalZ != null && historicalZ.size() >= 9) {
            boolean allPositive = (z0 > 0.0);
            boolean allNegative = (z0 < 0.0);

            for (int i = 0; i < 9; i++) {
                double val = historicalZ.get(i).doubleValue();
                if (val <= 0.0) allPositive = false;
                if (val >= 0.0) allNegative = false;
            }

            if (allPositive || allNegative) {
                return WestgardEvaluationResult.builder()
                        .zScore(currentZ)
                        .status("REJECTED")
                        .violatedRule("10_x")
                        .lockout(false)
                        .description("Rule 10-x Violated: Ten consecutive controls on same side of mean (Systematic Bias).")
                        .build();
            }
        }

        // Rule 1_2s: Warning rule - Single control > 2s
        if (Math.abs(z0) > 2.0) {
            return WestgardEvaluationResult.builder()
                    .zScore(currentZ)
                    .status("WARNING")
                    .violatedRule("1_2s")
                    .lockout(false)
                    .description("Rule 1-2s Triggered: Measurement exceeds 2 standard deviations (Warning).")
                    .build();
        }

        return WestgardEvaluationResult.builder()
                .zScore(currentZ)
                .status("IN_CONTROL")
                .violatedRule(null)
                .lockout(false)
                .description("Run In-Control")
                .build();
    }
}
