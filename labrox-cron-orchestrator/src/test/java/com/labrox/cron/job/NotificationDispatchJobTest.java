package com.labrox.cron.job;

import com.labrox.cron.domain.CronJobExecution;
import com.labrox.cron.domain.NotificationQueueItem;
import com.labrox.cron.repository.NotificationQueueRepository;
import com.labrox.cron.service.CronExecutionTrackerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDispatchJobTest {

    @Mock
    private CronExecutionTrackerService trackerService;

    @Mock
    private NotificationQueueRepository queueRepository;

    @InjectMocks
    private NotificationDispatchJob notificationJob;

    @Test
    @DisplayName("Process pending notifications and mark as SENT")
    void testDispatchPendingNotifications() {
        CronJobExecution mockExec = CronJobExecution.builder().id(301L).build();
        when(trackerService.startExecution("NotificationDispatchJob")).thenReturn(mockExec);

        NotificationQueueItem item1 = NotificationQueueItem.builder()
                .id(1L)
                .channel("SMS")
                .recipient("+919876543210")
                .templateCode("REPORT_READY_SMS")
                .payloadJson("{\"name\":\"John\"}")
                .status("PENDING")
                .retryCount(0)
                .build();

        NotificationQueueItem item2 = NotificationQueueItem.builder()
                .id(2L)
                .channel("WHATSAPP")
                .recipient("+919876543211")
                .templateCode("REPORT_PDF_WA")
                .payloadJson("{\"name\":\"Jane\"}")
                .status("PENDING")
                .retryCount(0)
                .build();

        when(queueRepository.findByStatusAndRetryCountLessThan(eq("PENDING"), eq(3), any()))
                .thenReturn(List.of(item1, item2));

        int sent = notificationJob.executeJob();

        assertEquals(2, sent);
        assertEquals("SENT", item1.getStatus());
        assertEquals("SENT", item2.getStatus());
        verify(queueRepository, times(2)).save(any(NotificationQueueItem.class));
        verify(trackerService).completeExecution(eq(301L), eq(2));
    }
}
