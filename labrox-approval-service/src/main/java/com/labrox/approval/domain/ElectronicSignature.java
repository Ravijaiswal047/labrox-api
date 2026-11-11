package com.labrox.approval.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "electronic_signature", schema = "approval_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "test_id", nullable = false)
    private Long testId;

    @Column(name = "signer_id", nullable = false)
    private Integer signerId;

    @Column(name = "signer_name", nullable = false, length = 100)
    private String signerName;

    @Column(name = "registration_number", nullable = false, length = 60)
    private String registrationNumber;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(name = "signature_meaning", nullable = false, length = 255)
    private String signatureMeaning;

    @Column(name = "signed_at")
    @Builder.Default
    private Instant signedAt = Instant.now();

    @Column(name = "signature_hash", nullable = false, length = 64)
    private String signatureHash;

    @Column(name = "is_valid", nullable = false)
    @Builder.Default
    private boolean valid = true;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
