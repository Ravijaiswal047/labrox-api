package com.labrox.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleRejectRequest {
    private Integer companyId;
    private String barcodeNo;
    private Integer reasonId;
    private String remarks;
    private Integer rejectedBy;
}
