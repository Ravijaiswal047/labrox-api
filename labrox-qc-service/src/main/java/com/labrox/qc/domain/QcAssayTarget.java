package com.labrox.qc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "qc_assay_target", schema = "qc_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QcAssayTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lot_id", nullable = false)
    private Integer lotId;

    @Column(name = "observation_id", nullable = false)
    private Integer observationId;

    @Column(name = "observation_name", nullable = false, length = 100)
    private String observationName;

    @Column(name = "target_mean", nullable = false, precision = 12, scale = 4)
    private BigDecimal targetMean;

    @Column(name = "target_sd", nullable = false, precision = 12, scale = 4)
    private BigDecimal targetSd;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
