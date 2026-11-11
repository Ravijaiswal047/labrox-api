package com.labrox.qc.repository;

import com.labrox.qc.domain.QcAssayTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QcAssayTargetRepository extends JpaRepository<QcAssayTarget, Integer> {
    List<QcAssayTarget> findByLotId(Integer lotId);
    Optional<QcAssayTarget> findByLotIdAndObservationId(Integer lotId, Integer observationId);
}
