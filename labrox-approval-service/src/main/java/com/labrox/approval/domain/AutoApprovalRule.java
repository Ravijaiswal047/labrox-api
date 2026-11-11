package com.labrox.approval.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "auto_approval_rule", schema = "approval_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoApprovalRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "investigation_id")
    private Integer investigationId;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "allow_delta_warning", nullable = false)
    private boolean allowDeltaWarning;

    @Column(name = "allow_panic_alarm", nullable = false)
    private boolean allowPanicAlarm;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
