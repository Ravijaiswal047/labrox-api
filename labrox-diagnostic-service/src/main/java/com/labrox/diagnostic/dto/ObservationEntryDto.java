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
public class ObservationEntryDto {
    private Integer observationId;
    private String observationName;
    private String resultValue;
    private BigDecimal numericValue;
    private String unit;
    private String comment;
}
