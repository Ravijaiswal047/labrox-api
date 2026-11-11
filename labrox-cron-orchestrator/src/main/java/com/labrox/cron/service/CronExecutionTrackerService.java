package com.labrox.cron.service;

import com.labrox.cron.domain.CronJobDefinition;
import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.repository.CronJobDefinitionRepository;
import com.labrox.cron.repository.CronJobExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronExecutionTrackerService {

    private final CronJobExecutionRepository executionRepository;
    private final CronJobDefinitionRepository definitionRepository;

    @Transactional
    public CronJobExecution startExecution(String jobName) {
        CronJobExecution execution = CronJobExecution.builder()
                .jobName(jobName)
                .status("RUNNING")
                .startTime(Instant.now())
                .recordsProcessed(0)
                .build();

        CronJobExecution saved = executionRepository.save(execution);
        log.info("Background job {} started with ExecutionId={}", jobName, saved.getId());
        return saved;
    }

    @Transactional
    public void completeExecution(Long executionId, int recordsProcessed) {
        Optional<CronJobExecution> execOpt = executionRepository.findById(executionId);
        if (execOpt.isEmpty()) return;

        CronJobExecution exec = execOpt.get();
        Instant now = Instant.now();
        exec.setEndTime(now);
        exec.setStatus("SUCCESS");
        exec.setRecordsProcessed(recordsProcessed);
        exec.setDurationMs(Duration.between(exec.getStartTime(), now).toMillis());
        executionRepository.save(exec);

        // Update definition lastRunTime
        definitionRepository.findByJobName(exec.getJobName()).ifPresent(def -> {
            def.setLastRunTime(now);
            def.setLastStatus("SUCCESS");
            definitionRepository.save(def);
        });

        log.info("Background job {} completed successfully in {} ms (Records: {})",
                exec.getJobName(), exec.getDurationMs(), recordsProcessed);
    }

    @Transactional
    public void failExecution(Long executionId, String errorMessage) {
        Optional<CronJobExecution> execOpt = executionRepository.findById(executionId);
        if (execOpt.isEmpty()) return;

        CronJobExecution exec = execOpt.get();
        Instant now = Instant.now();
        exec.setEndTime(now);
        exec.setStatus("FAILED");
        exec.setErrorMessage(errorMessage);
        exec.setDurationMs(Duration.between(exec.getStartTime(), now).toMillis());
        executionRepository.save(exec);

        definitionRepository.findByJobName(exec.getJobName()).ifPresent(def -> {
            def.setLastRunTime(now);
            def.setLastStatus("FAILED");
            definitionRepository.save(def);
        });

        log.error("Background job {} FAILED after {} ms: {}", exec.getJobName(), exec.getDurationMs(), errorMessage);
    }
}
