package com.labrox.reporting.repository;

import com.labrox.reporting.domain.GeneratedReportArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneratedReportArchiveRepository extends JpaRepository<GeneratedReportArchive, Long> {
    Optional<GeneratedReportArchive> findByVerificationToken(String verificationToken);
    Optional<GeneratedReportArchive> findFirstByTestIdOrderByReportVersionDesc(Long testId);
}
