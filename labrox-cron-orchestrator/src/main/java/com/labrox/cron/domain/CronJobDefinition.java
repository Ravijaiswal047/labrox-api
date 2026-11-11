package com.labrox.cron.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "cron_job_definition", schema = "cron_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronJobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_name", nullable = false, unique = true, length = 100)
    private String jobName;

    @Column(name = "cron_expression", nullable = false, length = 60)
    private String cronExpression;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "last_run_time")
    private Instant lastRunTime;

    @Column(name = "next_run_time")
    private Instant nextRunTime;

    @Column(name = "last_status", length = 30)
    private String lastStatus;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
