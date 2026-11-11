package com.labrox.approval.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "amendment_history", schema = "approval_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "test_id", nullable = false)
    private Long testId;

    @Column(name = "observation_id", nullable = false)
    private Integer observationId;

    @Column(name = "observation_name", nullable = false, length = 150)
    private String observationName;

    @Column(name = "original_value", nullable = false, length = 100)
    private String originalValue;

    @Column(name = "amended_value", nullable = false, length = 100)
    private String amendedValue;

    @Column(name = "reason_for_change", nullable = false, columnDefinition = "TEXT")
    private String reasonForChange;

    @Column(name = "amended_by", nullable = false)
    private Integer amendedBy;

    @Column(name = "amended_by_name", nullable = false, length = 100)
    private String amendedByName;

    @Column(name = "amended_at")
    @Builder.Default
    private Instant amendedAt = Instant.now();

    @Column(name = "superseded_signature_id")
    private Long supersededSignatureId;
}
