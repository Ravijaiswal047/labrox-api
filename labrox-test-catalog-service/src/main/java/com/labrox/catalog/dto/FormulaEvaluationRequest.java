package com.labrox.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulaEvaluationRequest {
    private String formulaType; // INDIRECT_BILIRUBIN, GLOBULIN, AG_RATIO, ANION_GAP
    private Map<String, BigDecimal> inputs;
}
