package com.labrox.iam.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String employeeCode;
    private String name;
    private Integer companyId;
    private Integer defaultCentreId;
    private Integer currentCentreId;
    private Integer designationId;
    private List<AccessibleCentreDto> accessibleCentres;
    private UserPermissionsDto permissions;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessibleCentreDto {
        private Integer centreId;
        private String centreCode;
        private String centreName;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPermissionsDto {
        private boolean canRefund;
        private boolean canSettlement;
        private boolean canDiscountAfterBill;
        private boolean saveAmendment;
        private boolean blockExcelPrintout;
        private boolean blockLabReportPrintout;
        private boolean amrAccess;
        private boolean unmasking;
    }
}

