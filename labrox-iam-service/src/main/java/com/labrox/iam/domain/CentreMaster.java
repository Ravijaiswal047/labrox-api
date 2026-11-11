package com.labrox.iam.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "centre_master", schema = "iam_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CentreMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "centre_id")
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "centre_code", nullable = false)
    private String centreCode;

    @Column(name = "centre_name", nullable = false)
    private String centreName;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}

