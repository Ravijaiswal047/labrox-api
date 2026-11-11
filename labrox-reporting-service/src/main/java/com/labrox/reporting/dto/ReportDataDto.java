package com.labrox.reporting.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDataDto {
    private Long testId;
    private String barcodeNo;
    private String uhid;
    private String patientName;
    private Integer ageYears;
    private String gender;
    private String referredByDoctor;
    private String specimenType;
    private String collectionTime;
    private String approvalTime;
    private String testName;
    private String department;
    private String centerName;
    private String nablAccNo;
    private String addressLine;
    private String contactLine;
    private String pathologistName;
    private String medicalRegNo;
    private String signatureHash;
    private String verificationToken;
    private List<ReportObservationDto> observations;
}
