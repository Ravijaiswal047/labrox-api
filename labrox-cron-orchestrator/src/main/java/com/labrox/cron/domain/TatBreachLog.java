package com.labrox.cron.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "tat_breach_log", schema = "cron_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TatBreachLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_id", nullable = false)
    private Long testId;

    @Column(name = "target_tat_minutes", nullable = false)
    private Integer targetTatMinutes;

    @Column(name = "actual_elapsed_minutes", nullable = false)
    private Integer actualElapsedMinutes;

    @Column(name = "breach_time", nullable = false)
    @Builder.Default
    private Instant breachTime = Instant.now();

    @Column(name = "escalation_status", nullable = false, length = 30)
    @Builder.Default
    private String escalationStatus = "FLAGGED";
}
