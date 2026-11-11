package com.labrox.qc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeveyJenningsDataPoint {
    private Long runId;
    private Instant runTime;
    private String analyzerId;
    private BigDecimal measuredValue;
    private BigDecimal zScore;
    private String status;
    private String violatedRule;
    private boolean lockoutTriggered;
}
