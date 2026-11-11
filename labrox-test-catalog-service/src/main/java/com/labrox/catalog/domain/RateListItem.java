package com.labrox.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "f_ratelist", schema = "catalog_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "rate_type_id", nullable = false)
    private Integer rateTypeId;

    @Column(name = "investigation_id", nullable = false)
    private Integer investigationId;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal rate;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
