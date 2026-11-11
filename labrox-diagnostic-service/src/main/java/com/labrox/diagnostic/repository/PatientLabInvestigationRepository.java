package com.labrox.diagnostic.repository;

import com.labrox.diagnostic.domain.PatientLabInvestigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientLabInvestigationRepository extends JpaRepository<PatientLabInvestigation, Long> {
    Optional<PatientLabInvestigation> findByCompanyIdAndTestId(Integer companyId, Long testId);
    List<PatientLabInvestigation> findByCompanyIdAndBarcodeNo(Integer companyId, String barcodeNo);
    List<PatientLabInvestigation> findByCompanyIdAndPatientId(Integer companyId, Integer patientId);
    List<PatientLabInvestigation> findByCompanyIdAndStatus(Integer companyId, String status);

    @Query("""
        SELECT i FROM PatientLabInvestigation i
        LEFT JOIN FETCH i.observations
        WHERE i.companyId = :companyId AND i.testId = :testId
    """)
    Optional<PatientLabInvestigation> findWithObservations(
            @Param("companyId") Integer companyId,
            @Param("testId") Long testId);
}
