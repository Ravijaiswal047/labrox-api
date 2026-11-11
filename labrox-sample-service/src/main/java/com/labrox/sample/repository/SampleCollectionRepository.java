package com.labrox.sample.repository;

import com.labrox.sample.domain.SampleCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SampleCollectionRepository extends JpaRepository<SampleCollection, Long> {
    Optional<SampleCollection> findByCompanyIdAndBarcodeNo(Integer companyId, String barcodeNo);
    List<SampleCollection> findByCompanyIdAndOrderId(Integer companyId, String orderId);
    List<SampleCollection> findByCompanyIdAndStatus(Integer companyId, String status);
}
