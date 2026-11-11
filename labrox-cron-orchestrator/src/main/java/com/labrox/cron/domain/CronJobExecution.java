package com.labrox.cron.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "cron_job_execution", schema = "cron_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronJobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(nullable = false, length = 30)
    private String status; // RUNNING, SUCCESS, FAILED

    @Column(name = "start_time", nullable = false)
    @Builder.Default
    private Instant startTime = Instant.now();

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "records_processed")
    private Integer recordsProcessed;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
