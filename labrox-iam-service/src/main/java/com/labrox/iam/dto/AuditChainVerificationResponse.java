package com.labrox.iam.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditChainVerificationResponse {
    private boolean valid;
    private int totalRecordsVerified;
    private Long corruptedRecordId;
    private String message;
}

