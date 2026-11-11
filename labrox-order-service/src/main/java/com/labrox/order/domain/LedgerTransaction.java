package com.labrox.order.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "f_ledgertransaction", schema = "order_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_transaction_id")
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "ledger_transaction_no", nullable = false, unique = true, length = 64)
    private String ledgerTransactionNo;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "centre_id", nullable = false)
    private Integer centreId;

    @Column(name = "rate_type_id", nullable = false)
    private Integer rateTypeId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "doctor_name", length = 150)
    private String doctorName;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "discount_reason", length = 255)
    private String discountReason;

    @Column(name = "discount_approved_by")
    private Integer discountApprovedBy;

    @Column(name = "net_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "adjustment", nullable = false, precision = 12, scale = 2)
    private BigDecimal adjustment;

    @Column(name = "balance_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceAmount;

    @Column(name = "is_credit")
    private Boolean isCredit;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
