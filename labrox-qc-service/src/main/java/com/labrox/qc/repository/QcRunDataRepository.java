package com.labrox.qc.repository;

import com.labrox.qc.domain.QcRunData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcRunDataRepository extends JpaRepository<QcRunData, Long> {

    @Query("""
        SELECT r FROM QcRunData r
        WHERE r.assayTargetId = :targetId
        ORDER BY r.runTime DESC
    """)
    List<QcRunData> findRecentRuns(@Param("targetId") Integer targetId, Pageable pageable);

    List<QcRunData> findByAssayTargetIdOrderByRunTimeAsc(Integer assayTargetId);
}
