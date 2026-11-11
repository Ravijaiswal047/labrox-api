package com.labrox.order.repository;

import com.labrox.order.domain.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {
    Optional<LedgerTransaction> findByCompanyIdAndLedgerTransactionNo(Integer companyId, String ledgerTransactionNo);
    List<LedgerTransaction> findByCompanyIdAndPatientIdOrderByCreatedAtDesc(Integer companyId, Long patientId);
    long countByCompanyId(Integer companyId);
}
