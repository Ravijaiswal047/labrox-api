package com.labrox.diagnostic.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "patient_labobservation_opd", schema = "diagnostic_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientLabObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private PatientLabInvestigation investigation;

    @Column(name = "observation_id", nullable = false)
    private Integer observationId;

    @Column(name = "observation_name", nullable = false, length = 150)
    private String observationName;

    @Column(name = "result_value", length = 100)
    private String resultValue;

    @Column(name = "numeric_value", precision = 14, scale = 4)
    private BigDecimal numericValue;

    @Column(name = "old_value", precision = 14, scale = 4)
    private BigDecimal oldValue;

    @Column(name = "delta_percentage", precision = 6, scale = 2)
    private BigDecimal deltaPercentage;

    @Column(name = "delta_flag", length = 20)
    @Builder.Default
    private String deltaFlag = "OK"; // OK, DELTA_WARNING

    @Column(length = 50)
    private String unit;

    @Column(name = "min_reading", precision = 12, scale = 4)
    private BigDecimal minReading;

    @Column(name = "max_reading", precision = 12, scale = 4)
    private BigDecimal maxReading;

    @Column(name = "min_critical", precision = 12, scale = 4)
    private BigDecimal minCritical;

    @Column(name = "max_critical", precision = 12, scale = 4)
    private BigDecimal maxCritical;

    @Column(name = "display_reading", length = 100)
    private String displayReading;

    @Column(length = 20)
    @Builder.Default
    private String flag = "NORMAL"; // NORMAL, LOW, HIGH, CRITICAL_LOW, CRITICAL_HIGH

    @Column(name = "is_calculated", nullable = false)
    private boolean calculated;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedAt = Instant.now();
    }
}
