package com.labrox.reporting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "generated_report_archive", schema = "reporting_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedReportArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_id", nullable = false)
    private Long testId;

    @Column(name = "barcode_no", nullable = false, length = 32)
    private String barcodeNo;

    @Column(name = "report_version", nullable = false)
    private Integer reportVersion = 1;

    @Column(name = "verification_token", nullable = false, unique = true, length = 64)
    private String verificationToken;

    @Column(name = "signature_stamp_hash", length = 128)
    private String signatureStampHash;

    @Column(name = "file_storage_path", nullable = false, length = 500)
    private String fileStoragePath;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "rendered_by_user_id", nullable = false)
    private Long renderedByUserId;

    @CreationTimestamp
    @Column(name = "rendered_at", nullable = false, updatable = false)
    private LocalDateTime renderedAt;
}
