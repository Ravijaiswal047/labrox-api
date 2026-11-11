package com.labrox.approval.repository;

import com.labrox.approval.domain.AmendmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmendmentHistoryRepository extends JpaRepository<AmendmentHistory, Long> {
    List<AmendmentHistory> findByCompanyIdAndTestIdOrderByAmendedAtDesc(Integer companyId, Long testId);
}
