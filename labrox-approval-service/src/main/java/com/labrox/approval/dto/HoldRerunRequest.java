package com.labrox.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldRerunRequest {
    private Integer companyId;
    private Long testId;
    private Integer userId;
    private String userName;
    private String action; // HOLD, RERUN
    private String reason;
}
