package com.labrox.reporting.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportObservationDto {
    private String analyteName;
    private String resultValue;
    private String unit;
    private String biologicalInterval;
    private String flag; // NORMAL, HIGH, LOW, PANIC
    private boolean abnormal;
}
