package com.labrox.catalog.repository;

import com.labrox.catalog.domain.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestigationRepository extends JpaRepository<Investigation, Integer> {
    List<Investigation> findByCompanyIdAndActiveTrue(Integer companyId);
    Optional<Investigation> findByCompanyIdAndTestCodeAndActiveTrue(Integer companyId, String testCode);
    List<Investigation> findByCompanyIdAndTestNameContainingIgnoreCaseAndActiveTrue(Integer companyId, String query);
}
