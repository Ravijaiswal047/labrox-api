package com.labrox.catalog.repository;

import com.labrox.catalog.domain.RateListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RateListRepository extends JpaRepository<RateListItem, Integer> {
    Optional<RateListItem> findByCompanyIdAndRateTypeIdAndInvestigationIdAndActiveTrue(
            Integer companyId, Integer rateTypeId, Integer investigationId);
}
