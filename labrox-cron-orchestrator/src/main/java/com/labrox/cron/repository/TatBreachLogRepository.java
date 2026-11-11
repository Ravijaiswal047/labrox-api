package com.labrox.cron.repository;

import com.labrox.cron.domain.TatBreachLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TatBreachLogRepository extends JpaRepository<TatBreachLog, Long> {
    List<TatBreachLog> findByEscalationStatus(String escalationStatus);
}
