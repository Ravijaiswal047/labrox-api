package com.labrox.order.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "f_reciept", schema = "order_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "receipt_no", nullable = false, unique = true, length = 64)
    private String receiptNo;

    @Column(name = "ledger_transaction_no", nullable = false, length = 64)
    private String ledgerTransactionNo;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_mode", nullable = false, length = 50)
    private String paymentMode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "card_no", length = 20)
    private String cardNo;

    @Column(name = "transaction_no", length = 100)
    private String transactionNo;

    @Column(name = "centre_id", nullable = false)
    private Integer centreId;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
