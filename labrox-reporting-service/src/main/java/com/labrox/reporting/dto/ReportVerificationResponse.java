package com.labrox.reporting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportVerificationResponse {
    private boolean authentic;
    private String verificationToken;
    private Long testId;
    private String barcodeNo;
    private Integer reportVersion;
    private String signatureStampHash;
    private LocalDateTime renderedAt;
    private String issuingAuthority;
    private String message;
}
