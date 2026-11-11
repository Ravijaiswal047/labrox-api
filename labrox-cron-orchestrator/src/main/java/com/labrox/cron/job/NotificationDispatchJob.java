package com.labrox.cron.job;

import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.domain.NotificationQueueItem;
import com.labrox.cron.repository.NotificationQueueRepository;
import com.labrox.cron.service.CronExecutionTrackerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchJob {

    private final CronExecutionTrackerService trackerService;
    private final NotificationQueueRepository queueRepository;

    public int executeJob() {
        CronJobExecution execution = trackerService.startExecution("NotificationDispatchJob");
        int sentCount = 0;

        try {
            List<NotificationQueueItem> pending = queueRepository.findByStatusAndRetryCountLessThan("PENDING", 3, PageRequest.of(0, 50));

            for (NotificationQueueItem item : pending) {
                try {
                    // Simulate carrier transmission (SMS Gateway / SMTP / WhatsApp Business API)
                    log.info("Dispatching notification: Channel={}, Recipient={}, Template={}",
                            item.getChannel(), item.getRecipient(), item.getTemplateCode());

                    item.setStatus("SENT");
                    item.setSentAt(Instant.now());
                    queueRepository.save(item);
                    sentCount++;
                } catch (Exception dispatchError) {
                    item.setRetryCount(item.getRetryCount() + 1);
                    if (item.getRetryCount() >= 3) {
                        item.setStatus("FAILED");
                    }
                    queueRepository.save(item);
                    log.warn("Notification dispatch failed for Recipient={}, RetryCount={}", item.getRecipient(), item.getRetryCount());
                }
            }

            trackerService.completeExecution(execution.getId(), sentCount);
        } catch (Exception e) {
            trackerService.failExecution(execution.getId(), e.getMessage());
            log.error("NotificationDispatchJob error", e);
        }

        return sentCount;
    }
}
