package com.labrox.approval.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "test_approval_log", schema = "approval_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestApprovalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "test_id", nullable = false)
    private Long testId;

    @Column(nullable = false, length = 30)
    private String action; // PROVISIONAL_APPROVE, FINAL_APPROVE, AUTO_APPROVE, HOLD, RERUN, AMEND

    @Column(name = "approver_id", nullable = false)
    private Integer approverId;

    @Column(name = "approver_name", nullable = false, length = 100)
    private String approverName;

    @Column(name = "approver_role", nullable = false, length = 50)
    private String approverRole; // RESIDENT, PATHOLOGIST, SYSTEM

    @Column(name = "signature_hash", length = 64)
    private String signatureHash;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
