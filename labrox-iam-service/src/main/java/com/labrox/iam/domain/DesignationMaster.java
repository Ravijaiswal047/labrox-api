package com.labrox.iam.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "designation_master", schema = "iam_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id")
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "designation_name", nullable = false)
    private String designationName;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}

