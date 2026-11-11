package com.labrox.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoApprovalDecision {
    private Long testId;
    private boolean eligible;
    private String reason;
}
