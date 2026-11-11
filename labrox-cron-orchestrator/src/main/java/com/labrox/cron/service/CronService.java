package com.labrox.cron.service;

import com.labrox.cron.domain.CronJobDefinition;
import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.job.BulkReportGenerationJob;
import com.labrox.cron.job.NotificationDispatchJob;
import com.labrox.cron.job.TatBreachMonitorJob;
import com.labrox.cron.repository.CronJobDefinitionRepository;
import com.labrox.cron.repository.CronJobExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronService {

    private final CronJobDefinitionRepository definitionRepository;
    private final CronJobExecutionRepository executionRepository;
    private final BulkReportGenerationJob bulkReportJob;
    private final NotificationDispatchJob notificationJob;
    private final TatBreachMonitorJob tatBreachJob;

    public List<CronJobDefinition> getAllJobDefinitions() {
        return definitionRepository.findAll();
    }

    public List<CronJobExecution> getRecentExecutions() {
        return executionRepository.findTop20ByOrderByStartTimeDesc();
    }

    public CompletableFuture<Integer> triggerJobManually(String jobName) {
        log.info("Manually dispatching background job: {}", jobName);

        return CompletableFuture.supplyAsync(() -> {
            switch (jobName) {
                case "BulkReportGenerationJob":
                    return bulkReportJob.executeJob();
                case "NotificationDispatchJob":
                    return notificationJob.executeJob();
                case "TatBreachMonitorJob":
                    return tatBreachJob.executeJob();
                default:
                    log.warn("Job {} has simulated standard execution", jobName);
                    return 0;
            }
        });
    }
}
