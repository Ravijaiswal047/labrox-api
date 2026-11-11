package com.labrox.reporting.repository;

import com.labrox.reporting.domain.ReportTemplateHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportTemplateHeaderRepository extends JpaRepository<ReportTemplateHeader, Long> {
    Optional<ReportTemplateHeader> findFirstByCentreIdAndActiveTrue(Long centreId);
    Optional<ReportTemplateHeader> findFirstByActiveTrue();
}
