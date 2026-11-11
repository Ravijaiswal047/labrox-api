package com.labrox.sample.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "sample_collection", schema = "sample_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "sample_type_id", nullable = false)
    private Integer sampleTypeId;

    @Column(name = "sample_type_name", nullable = false, length = 100)
    private String sampleTypeName;

    @Column(name = "tube_color", nullable = false, length = 50)
    private String tubeColor;

    @Column(nullable = false, length = 30)
    private String status; // COLLECTED, RECEIVED, REJECTED, IN_TRANSIT

    @Column(name = "collection_time")
    private Instant collectionTime;

    @Column(name = "collected_by")
    private Integer collectedBy;

    @Column(name = "received_time")
    private Instant receivedTime;

    @Column(name = "received_by")
    private Integer receivedBy;

    @Column(name = "rejection_reason_id")
    private Integer rejectionReasonId;

    @Column(name = "rejection_remarks", columnDefinition = "TEXT")
    private String rejectionRemarks;

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
        if (collectionTime == null) collectionTime = Instant.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedAt = Instant.now();
    }
}
