package com.labrox.sample.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sample_rejection_reason", schema = "sample_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleRejectionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reason_code", nullable = false, unique = true, length = 30)
    private String reasonCode;

    @Column(name = "reason_description", nullable = false, length = 255)
    private String reasonDescription;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
