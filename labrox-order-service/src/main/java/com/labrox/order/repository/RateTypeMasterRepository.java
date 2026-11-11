package com.labrox.order.repository;

import com.labrox.order.domain.RateTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateTypeMasterRepository extends JpaRepository<RateTypeMaster, Integer> {
    List<RateTypeMaster> findByCompanyIdAndActiveTrueOrderByRateTypeName(Integer companyId);
}
