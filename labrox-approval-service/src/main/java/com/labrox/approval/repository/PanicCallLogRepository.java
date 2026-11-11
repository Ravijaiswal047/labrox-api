package com.labrox.approval.repository;

import com.labrox.approval.domain.PanicCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanicCallLogRepository extends JpaRepository<PanicCallLog, Long> {
    List<PanicCallLog> findByCompanyIdAndTestId(Integer companyId, Long testId);
}
