package com.labrox.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {
    @NotNull
    private Integer companyId;
    @NotBlank
    private String entityName;
    @NotBlank
    private String entityId;
    @NotBlank
    private String action; // CREATE, UPDATE, APPROVE, AMEND
    private String previousValue;
    private String newValue;
    private String changeReason;
    @NotNull
    private Integer userId;
    private String userIp;
    private String signature;
}

