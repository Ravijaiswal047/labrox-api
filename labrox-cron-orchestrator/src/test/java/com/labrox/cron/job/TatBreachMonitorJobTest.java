package com.labrox.cron.job;

import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.domain.TatBreachLog;
import com.labrox.cron.repository.TatBreachLogRepository;
import com.labrox.cron.service.CronExecutionTrackerService;
import com.labrox.diagnostic.domain.PatientLabInvestigation;
import com.labrox.diagnostic.repository.PatientLabInvestigationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TatBreachMonitorJobTest {

    @Mock
    private CronExecutionTrackerService trackerService;

    @Mock
    private PatientLabInvestigationRepository investigationRepository;

    @Mock
    private TatBreachLogRepository breachLogRepository;

    @InjectMocks
    private TatBreachMonitorJob tatBreachJob;

    @Test
    @DisplayName("Detect investigations exceeding target TAT and log breach")
    void testTatBreachDetection() {
        CronJobExecution mockExec = CronJobExecution.builder().id(201L).build();
        when(trackerService.startExecution("TatBreachMonitorJob")).thenReturn(mockExec);

        // Investigation created 180 minutes ago (target is 120 minutes)
        PatientLabInvestigation delayed = PatientLabInvestigation.builder()
                .testId(8001L)
                .companyId(1)
                .status("SAMPLE_RECEIVED")
                .createdAt(Instant.now().minusSeconds(180 * 60))
                .build();

        // Investigation created 30 minutes ago (within target)
        PatientLabInvestigation onTime = PatientLabInvestigation.builder()
                .testId(8002L)
                .companyId(1)
                .status("SAMPLE_RECEIVED")
                .createdAt(Instant.now().minusSeconds(30 * 60))
                .build();

        when(investigationRepository.findByCompanyIdAndStatus(1, "SAMPLE_RECEIVED"))
                .thenReturn(List.of(delayed, onTime));

        int breached = tatBreachJob.executeJob();

        assertEquals(1, breached, "Only 1 delayed investigation should trigger a TAT breach log");
        verify(breachLogRepository, times(1)).save(any(TatBreachLog.class));
        verify(trackerService).completeExecution(eq(201L), eq(1));
    }
}
