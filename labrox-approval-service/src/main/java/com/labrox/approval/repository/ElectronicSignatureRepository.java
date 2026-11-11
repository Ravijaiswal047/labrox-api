package com.labrox.approval.repository;

import com.labrox.approval.domain.ElectronicSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectronicSignatureRepository extends JpaRepository<ElectronicSignature, Long> {
    List<ElectronicSignature> findByCompanyIdAndTestId(Integer companyId, Long testId);
    Optional<ElectronicSignature> findByCompanyIdAndTestIdAndValidTrue(Integer companyId, Long testId);
}
