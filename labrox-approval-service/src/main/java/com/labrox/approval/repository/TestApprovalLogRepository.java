package com.labrox.approval.repository;

import com.labrox.approval.domain.TestApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestApprovalLogRepository extends JpaRepository<TestApprovalLog, Long> {
    List<TestApprovalLog> findByCompanyIdAndTestIdOrderByCreatedAtDesc(Integer companyId, Long testId);
}
