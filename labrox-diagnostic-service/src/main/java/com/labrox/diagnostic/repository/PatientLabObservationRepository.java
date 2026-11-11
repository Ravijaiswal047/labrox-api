package com.labrox.diagnostic.repository;

import com.labrox.diagnostic.domain.PatientLabObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientLabObservationRepository extends JpaRepository<PatientLabObservation, Long> {
    List<PatientLabObservation> findByInvestigationTestId(Long testId);

    @Query("""
        SELECT obs FROM PatientLabObservation obs
        JOIN obs.investigation inv
        WHERE inv.companyId = :companyId
          AND inv.patientId = :patientId
          AND obs.observationId = :obsId
          AND obs.numericValue IS NOT NULL
        ORDER BY inv.createdAt DESC
    """)
    List<PatientLabObservation> findHistoricalReadings(
            @Param("companyId") Integer companyId,
            @Param("patientId") Integer patientId,
            @Param("obsId") Integer observationId);
}
