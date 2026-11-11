package com.labrox.order.repository;

import com.labrox.order.domain.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByCompanyIdAndReceiptNo(Integer companyId, String receiptNo);
    List<Receipt> findByCompanyIdAndLedgerTransactionNo(Integer companyId, String ledgerTransactionNo);
    long countByCompanyId(Integer companyId);
}
