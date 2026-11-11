package com.labrox.cron.job;

import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.domain.TatBreachLog;
import com.labrox.cron.repository.TatBreachLogRepository;
import com.labrox.cron.service.CronExecutionTrackerService;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TatBreachMonitorJob {

    private final CronExecutionTrackerService trackerService;
    private final PatientLabInvestigationRepository investigationRepository;
    private final TatBreachLogRepository breachLogRepository;

    private static final int DEFAULT_TARGET_TAT_MINUTES = 120; // 2 hours target TAT

    public int executeJob() {
        CronJobExecution execution = trackerService.startExecution("TatBreachMonitorJob");
        int breachedCount = 0;

        try {
            // Find investigations still in process
            List<PatientLabInvestigation> activeTests = investigationRepository.findByCompanyIdAndStatus(1, "SAMPLE_RECEIVED");
            Instant now = Instant.now();

            for (PatientLabInvestigation test : activeTests) {
                if (test.getCreatedAt() != null) {
                    long elapsedMinutes = Duration.between(test.getCreatedAt(), now).toMinutes();
                    if (elapsedMinutes > DEFAULT_TARGET_TAT_MINUTES) {
                        TatBreachLog breach = TatBreachLog.builder()
                                .testId(test.getTestId())
                                .targetTatMinutes(DEFAULT_TARGET_TAT_MINUTES)
                                .actualElapsedMinutes((int) elapsedMinutes)
                                .breachTime(now)
                                .escalationStatus("FLAGGED")
                                .build();
                        breachLogRepository.save(breach);
                        breachedCount++;
                    }
                }
            }

            trackerService.completeExecution(execution.getId(), breachedCount);
        } catch (Exception e) {
            trackerService.failExecution(execution.getId(), e.getMessage());
            log.error("TatBreachMonitorJob error", e);
        }

        return breachedCount;
    }
}
