package com.labrox.cron.repository;

import com.labrox.cron.domain.CronJobExecution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CronJobExecutionRepository extends JpaRepository<CronJobExecution, Long> {
    List<CronJobExecution> findByJobNameOrderByStartTimeDesc(String jobName, Pageable pageable);
    List<CronJobExecution> findTop20ByOrderByStartTimeDesc();
}
