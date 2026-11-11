package com.labrox.cron.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.cron.domain.CronJobDefinition;
import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.service.CronService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cron")
@RequiredArgsConstructor
@Tag(name = "Background Automation & Cron Orchestrator API", description = "Endpoints for clustered Quartz cron jobs, execution tracking, and manual job dispatch")
public class CronController {

    private final CronService cronService;

    @GetMapping("/jobs")
    @Operation(summary = "List all configured background automation jobs")
    public ResponseEntity<ApiResponse<List<CronJobDefinition>>> listJobs() {
        return ResponseEntity.ok(ApiResponse.ok(cronService.getAllJobDefinitions()));
    }

    @GetMapping("/executions")
    @Operation(summary = "List recent background job execution logs")
    public ResponseEntity<ApiResponse<List<CronJobExecution>>> listExecutions() {
        return ResponseEntity.ok(ApiResponse.ok(cronService.getRecentExecutions()));
    }

    @PostMapping("/jobs/{jobName}/trigger")
    @Operation(summary = "Manually trigger immediate execution of a background job")
    public ResponseEntity<ApiResponse<String>> triggerJob(@PathVariable("jobName") String jobName) {
        cronService.triggerJobManually(jobName);
        return ResponseEntity.ok(ApiResponse.ok("Job '" + jobName + "' triggered in background thread", jobName));
    }
}
