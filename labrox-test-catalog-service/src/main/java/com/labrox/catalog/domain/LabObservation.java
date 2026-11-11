package com.labrox.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "labobservation_master", schema = "catalog_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "observation_id")
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "observation_name", nullable = false)
    private String observationName;

    @Column(name = "reading_format", nullable = false)
    private String readingFormat;

    @Column(name = "decimal_places", nullable = false)
    private Integer decimalPlaces;

    @Column(name = "default_value")
    private String defaultValue;

    private String formula;
    private String unit;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
