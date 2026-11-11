package com.labrox.qc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "qc_run_data", schema = "qc_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QcRunData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assay_target_id", nullable = false)
    private Integer assayTargetId;

    @Column(name = "run_time", nullable = false)
    @Builder.Default
    private Instant runTime = Instant.now();

    @Column(name = "analyzer_id", nullable = false, length = 50)
    private String analyzerId;

    @Column(name = "measured_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal measuredValue;

    @Column(name = "z_score", nullable = false, precision = 6, scale = 3)
    private BigDecimal zScore;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "IN_CONTROL"; // IN_CONTROL, WARNING, REJECTED

    @Column(name = "violated_rule", length = 50)
    private String violatedRule;

    @Column(name = "lockout_triggered", nullable = false)
    private boolean lockoutTriggered;

    @Column(name = "corrective_action", columnDefinition = "TEXT")
    private String correctiveAction;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
