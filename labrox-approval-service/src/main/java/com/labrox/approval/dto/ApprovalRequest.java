package com.labrox.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {
    private Integer companyId;
    private Long testId;
    private Integer approverId;
    private String approverName;
    private String approverRole; // RESIDENT, PATHOLOGIST, SYSTEM
    private String registrationNumber;
    private String designation;
    private String pin; // Dual-factor credential
    private String signatureMeaning;
    private String comments;
}
