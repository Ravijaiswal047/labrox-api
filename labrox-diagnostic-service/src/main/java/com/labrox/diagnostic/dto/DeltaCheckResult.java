package com.labrox.diagnostic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltaCheckResult {
    private BigDecimal currentValue;
    private BigDecimal previousValue;
    private BigDecimal deltaPercentage;
    private String flag; // OK, DELTA_WARNING
    private boolean warning;
}
