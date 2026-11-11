package com.labrox.diagnostic.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient_labinvestigation_opd", schema = "diagnostic_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientLabInvestigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "barcode_no", nullable = false, length = 64)
    private String barcodeNo;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "patient_name", length = 150)
    private String patientName;

    @Column(name = "patient_age_days", nullable = false)
    private Integer patientAgeDays;

    @Column(name = "patient_gender", nullable = false, length = 20)
    private String patientGender;

    @Column(name = "investigation_id", nullable = false)
    private Integer investigationId;

    @Column(name = "test_code", nullable = false, length = 50)
    private String testCode;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(nullable = false, length = 30)
    private String status; // SAMPLE_RECEIVED, RESULT_ENTERED, TEST_APPROVED

    @Column(name = "is_critical", nullable = false)
    private boolean critical;

    @Column(name = "result_date")
    private Instant resultDate;

    @Column(name = "result_entered_by")
    private Integer resultEnteredBy;

    @OneToMany(mappedBy = "investigation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PatientLabObservation> observations = new ArrayList<>();

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
