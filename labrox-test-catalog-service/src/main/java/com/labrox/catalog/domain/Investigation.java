package com.labrox.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "investigation_master", schema = "catalog_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investigation_id")
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "test_code", nullable = false)
    private String testCode;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "sample_type_id", nullable = false)
    private Integer sampleTypeId;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(name = "is_package", nullable = false)
    private boolean isPackage;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
