package com.labrox.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceRangeEvaluationResult {
    private Integer observationId;
    private BigDecimal value;
    private String flag; // NORMAL, LOW, HIGH, CRITICAL_LOW, CRITICAL_HIGH, INDETERMINATE
    private BigDecimal minReading;
    private BigDecimal maxReading;
    private BigDecimal minCritical;
    private BigDecimal maxCritical;
    private String displayRange;
    private String interpretation;
}
