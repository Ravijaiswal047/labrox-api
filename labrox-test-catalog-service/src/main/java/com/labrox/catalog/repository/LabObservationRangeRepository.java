package com.labrox.catalog.repository;

import com.labrox.catalog.domain.LabObservationRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabObservationRangeRepository extends JpaRepository<LabObservationRange, Integer> {

    @Query("""
        SELECT r FROM LabObservationRange r
        WHERE r.observationId = :obsId
          AND (LOWER(r.gender) = LOWER(:gender) OR LOWER(r.gender) = 'both')
          AND :ageDays >= r.fromAgeDays AND :ageDays <= r.toAgeDays
        ORDER BY CASE WHEN LOWER(r.gender) = LOWER(:gender) THEN 0 ELSE 1 END
    """)
    List<LabObservationRange> findMatchingRanges(
            @Param("obsId") Integer observationId,
            @Param("gender") String gender,
            @Param("ageDays") Integer ageDays);

    List<LabObservationRange> findByObservationId(Integer observationId);
}
