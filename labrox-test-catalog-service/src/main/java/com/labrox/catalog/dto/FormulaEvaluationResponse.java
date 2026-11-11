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
public class FormulaEvaluationResponse {
    private String formulaType;
    private BigDecimal result;
    private boolean success;
    private String errorMessage;
}
