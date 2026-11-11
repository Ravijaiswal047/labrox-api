package com.labrox.qc.repository;

import com.labrox.qc.domain.QcControlLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QcControlLotRepository extends JpaRepository<QcControlLot, Integer> {
    List<QcControlLot> findByCompanyIdAndActiveTrue(Integer companyId);
    Optional<QcControlLot> findByCompanyIdAndLotNumber(Integer companyId, String lotNumber);
}
