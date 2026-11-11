package com.labrox.cron.repository;

import com.labrox.cron.domain.NotificationQueueItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationQueueRepository extends JpaRepository<NotificationQueueItem, Long> {
    List<NotificationQueueItem> findByStatusAndRetryCountLessThan(String status, int maxRetries, Pageable pageable);
}
