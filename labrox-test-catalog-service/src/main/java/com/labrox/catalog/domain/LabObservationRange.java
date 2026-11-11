package com.labrox.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "labobservation_range", schema = "catalog_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabObservationRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "range_id")
    private Integer id;

    @Column(name = "observation_id", nullable = false)
    private Integer observationId;

    @Column(nullable = false)
    private String gender; // Male, Female, Both

    @Column(name = "from_age_days", nullable = false)
    private Integer fromAgeDays;

    @Column(name = "to_age_days", nullable = false)
    private Integer toAgeDays;

    @Column(name = "min_reading", precision = 12, scale = 4)
    private BigDecimal minReading;

    @Column(name = "max_reading", precision = 12, scale = 4)
    private BigDecimal maxReading;

    @Column(name = "min_critical", precision = 12, scale = 4)
    private BigDecimal minCritical;

    @Column(name = "max_critical", precision = 12, scale = 4)
    private BigDecimal maxCritical;

    @Column(columnDefinition = "TEXT")
    private String interpretation;
}
