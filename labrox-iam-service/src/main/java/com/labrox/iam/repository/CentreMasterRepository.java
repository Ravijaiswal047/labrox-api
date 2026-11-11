package com.labrox.iam.repository;

import com.labrox.iam.domain.CentreMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CentreMasterRepository extends JpaRepository<CentreMaster, Integer> {
    List<CentreMaster> findByCompanyIdAndActiveTrue(Integer companyId);
}

