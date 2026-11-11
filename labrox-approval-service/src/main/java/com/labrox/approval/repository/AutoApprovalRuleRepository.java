package com.labrox.approval.repository;

import com.labrox.approval.domain.AutoApprovalRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutoApprovalRuleRepository extends JpaRepository<AutoApprovalRule, Integer> {
    Optional<AutoApprovalRule> findByCompanyIdAndInvestigationIdAndEnabledTrue(Integer companyId, Integer investigationId);
    Optional<AutoApprovalRule> findByCompanyIdAndDepartmentIdAndInvestigationIdIsNullAndEnabledTrue(Integer companyId, Integer departmentId);
}
