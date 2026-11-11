package com.labrox.iam.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "employee_master", schema = "iam_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    private String title;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_algo", nullable = false)
    private String passwordAlgo;

    @Column(name = "password_salt")
    private String passwordSalt;

    private String email;
    private String mobile;

    @Column(name = "default_centre_id", nullable = false)
    private Integer defaultCentreId;

    @Column(name = "designation_id", nullable = false)
    private Integer designationId;

    @Column(name = "rate_type_id")
    private Integer rateTypeId;

    @Column(name = "can_settlement", nullable = false)
    private boolean canSettlement;

    @Column(name = "can_refund", nullable = false)
    private boolean canRefund;

    @Column(name = "can_discount_after_bill", nullable = false)
    private boolean canDiscountAfterBill;

    @Column(name = "save_amendment", nullable = false)
    private boolean saveAmendment;

    @Column(name = "block_excel_printout", nullable = false)
    private boolean blockExcelPrintout;

    @Column(name = "block_lab_report_printout", nullable = false)
    private boolean blockLabReportPrintout;

    @Column(name = "amr_access", nullable = false)
    private boolean amrAccess;

    @Column(name = "unmasking", nullable = false)
    private boolean unmasking;

    @Column(name = "is_super_admin", nullable = false)
    private boolean superAdmin;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_password_changed", nullable = false)
    private boolean passwordChanged;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}

