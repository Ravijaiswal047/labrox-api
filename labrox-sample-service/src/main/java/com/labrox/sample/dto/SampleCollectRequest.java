package com.labrox.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleCollectRequest {
    private Integer companyId;
    private String orderId;
    private Integer patientId;
    private String patientName;
    private Integer sampleTypeId;
    private String sampleTypeName;
    private String tubeColor;
    private Integer collectorId;
}
