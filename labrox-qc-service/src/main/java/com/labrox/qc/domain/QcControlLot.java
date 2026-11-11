package com.labrox.qc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "qc_control_lot", schema = "qc_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QcControlLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "control_name", nullable = false, length = 100)
    private String controlName;

    @Column(name = "lot_number", nullable = false, length = 60)
    private String lotNumber;

    @Column(nullable = false)
    private Integer level; // 1=Low, 2=Normal, 3=High

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
