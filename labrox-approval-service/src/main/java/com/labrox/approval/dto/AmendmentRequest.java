package com.labrox.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentRequest {
    private Integer companyId;
    private Long testId;
    private Integer observationId;
    private String observationName;
    private String newValue;
    private String reasonForChange;
    private Integer pathologistId;
    private String pathologistName;
    private String registrationNumber;
    private String designation;
    private String pin;
}
