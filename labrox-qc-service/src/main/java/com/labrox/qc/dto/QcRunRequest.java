package com.labrox.qc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QcRunRequest {
    private Integer assayTargetId;
    private String analyzerId;
    private BigDecimal measuredValue;
    private String correctiveAction;
}
