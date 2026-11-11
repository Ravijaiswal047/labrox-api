package com.labrox.approval.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "panic_call_log", schema = "approval_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanicCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "test_id", nullable = false)
    private Long testId;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "observation_name", nullable = false, length = 150)
    private String observationName;

    @Column(name = "panic_value", nullable = false, length = 50)
    private String panicValue;

    @Column(name = "called_person_name", nullable = false, length = 100)
    private String calledPersonName;

    @Column(name = "called_person_designation", nullable = false, length = 100)
    private String calledPersonDesignation;

    @Column(name = "department_or_ward", nullable = false, length = 100)
    private String departmentOrWard;

    @Column(name = "call_time")
    @Builder.Default
    private Instant callTime = Instant.now();

    @Column(name = "caller_id", nullable = false)
    private Integer callerId;

    @Column(name = "call_status", nullable = false, length = 30)
    @Builder.Default
    private String callStatus = "INFORMED"; // INFORMED, UNREACHABLE, CALL_BACK_REQUESTED

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
