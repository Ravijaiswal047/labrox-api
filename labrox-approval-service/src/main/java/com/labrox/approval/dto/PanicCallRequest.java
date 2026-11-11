package com.labrox.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanicCallRequest {
    private Integer companyId;
    private Long testId;
    private Integer patientId;
    private String observationName;
    private String panicValue;
    private String calledPersonName;
    private String calledPersonDesignation;
    private String departmentOrWard;
    private Integer callerId;
    private String callStatus;
    private String remarks;
}
