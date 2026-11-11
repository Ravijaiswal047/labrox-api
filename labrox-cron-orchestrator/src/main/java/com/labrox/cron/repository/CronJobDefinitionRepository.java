package com.labrox.cron.repository;

import com.labrox.cron.domain.CronJobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CronJobDefinitionRepository extends JpaRepository<CronJobDefinition, Integer> {
    Optional<CronJobDefinition> findByJobName(String jobName);
    List<CronJobDefinition> findByEnabledTrue();
}
