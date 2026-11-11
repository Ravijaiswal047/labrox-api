package com.labrox.order.repository;

import com.labrox.order.domain.PatientMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMaster, Long> {
    Optional<PatientMaster> findByCompanyIdAndUhid(Integer companyId, String uhid);
    Optional<PatientMaster> findByCompanyIdAndMobile(Integer companyId, String mobile);
    long countByCompanyId(Integer companyId);
}
