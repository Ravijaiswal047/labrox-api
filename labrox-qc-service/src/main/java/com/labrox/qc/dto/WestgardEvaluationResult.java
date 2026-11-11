package com.labrox.qc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WestgardEvaluationResult {
    private BigDecimal zScore;
    private String status; // IN_CONTROL, WARNING, REJECTED
    private String violatedRule; // 1_2s, 1_3s, 2_2s, R_4s, 4_1s, 10_x, null
    private boolean lockout;
    private String description;
}
