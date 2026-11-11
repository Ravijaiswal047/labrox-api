package com.labrox.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "doctor_referal", schema = "catalog_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "doctor_code", nullable = false)
    private String doctorCode;

    @Column(name = "doctor_name", nullable = false)
    private String doctorName;

    private String mobile;
    private String email;
    private String specialization;

    @Column(name = "share_percentage", precision = 5, scale = 2)
    private BigDecimal sharePercentage;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
