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
public class RangeEvaluationRequest {
    private Integer observationId;
    private String gender;
    private Integer ageDays;
    private BigDecimal value;
}
