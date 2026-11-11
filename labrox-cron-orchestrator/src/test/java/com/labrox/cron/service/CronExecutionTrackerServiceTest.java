package com.labrox.cron.service;

import com.labrox.cron.domain.CronJobDefinition;
import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.repository.CronJobDefinitionRepository;
import com.labrox.cron.repository.CronJobExecutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CronExecutionTrackerServiceTest {

    @Mock
    private CronJobExecutionRepository executionRepository;

    @Mock
    private CronJobDefinitionRepository definitionRepository;

    @InjectMocks
    private CronExecutionTrackerService trackerService;

    @Test
    @DisplayName("Start execution saves initial RUNNING record")
    void testStartExecution() {
        when(executionRepository.save(any(CronJobExecution.class))).thenAnswer(i -> {
            CronJobExecution e = i.getArgument(0);
            e.setId(101L);
            return e;
        });

        CronJobExecution exec = trackerService.startExecution("BulkReportGenerationJob");

        assertNotNull(exec);
        assertEquals("RUNNING", exec.getStatus());
        assertEquals("BulkReportGenerationJob", exec.getJobName());
    }

    @Test
    @DisplayName("Complete execution sets SUCCESS, records count and duration")
    void testCompleteExecution() {
        CronJobExecution existing = CronJobExecution.builder()
                .id(101L)
                .jobName("BulkReportGenerationJob")
                .status("RUNNING")
                .startTime(Instant.now().minusSeconds(5))
                .build();

        CronJobDefinition def = CronJobDefinition.builder()
                .jobName("BulkReportGenerationJob")
                .build();

        when(executionRepository.findById(101L)).thenReturn(Optional.of(existing));
        when(definitionRepository.findByJobName("BulkReportGenerationJob")).thenReturn(Optional.of(def));

        trackerService.completeExecution(101L, 25);

        assertEquals("SUCCESS", existing.getStatus());
        assertEquals(25, existing.getRecordsProcessed());
        assertNotNull(existing.getDurationMs());
        assertTrue(existing.getDurationMs() >= 4000);
        verify(executionRepository).save(existing);
        verify(definitionRepository).save(def);
    }

    @Test
    @DisplayName("Fail execution sets FAILED and captures error message")
    void testFailExecution() {
        CronJobExecution existing = CronJobExecution.builder()
                .id(102L)
                .jobName("MachineDataSyncJob")
                .status("RUNNING")
                .startTime(Instant.now().minusSeconds(2))
                .build();

        when(executionRepository.findById(102L)).thenReturn(Optional.of(existing));

        trackerService.failExecution(102L, "Connection to analyzer COM3 timed out");

        assertEquals("FAILED", existing.getStatus());
        assertEquals("Connection to analyzer COM3 timed out", existing.getErrorMessage());
        verify(executionRepository).save(existing);
    }
}
