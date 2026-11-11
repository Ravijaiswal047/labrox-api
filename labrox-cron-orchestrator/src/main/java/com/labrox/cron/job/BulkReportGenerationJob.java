package com.labrox.cron.job;

import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.service.CronExecutionTrackerService;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BulkReportGenerationJob {

    private final CronExecutionTrackerService trackerService;
    private final PatientLabInvestigationRepository investigationRepository;

    public int executeJob() {
        CronJobExecution execution = trackerService.startExecution("BulkReportGenerationJob");
        int generatedCount = 0;

        try {
            // Find approved tests needing archival report generation
            List<PatientLabInvestigation> approvedTests = investigationRepository.findByCompanyIdAndStatus(1, "TEST_APPROVED");

            for (PatientLabInvestigation test : approvedTests) {
                // Simulates virtual-thread accelerated PDF compilation & cloud blob upload
                log.info("Batch compiled PDF report for TestId={}, Patient={}", test.getTestId(), test.getPatientName());
                generatedCount++;
            }

            trackerService.completeExecution(execution.getId(), generatedCount);
        } catch (Exception e) {
            trackerService.failExecution(execution.getId(), e.getMessage());
            log.error("BulkReportGenerationJob error", e);
        }

        return generatedCount;
    }
}
