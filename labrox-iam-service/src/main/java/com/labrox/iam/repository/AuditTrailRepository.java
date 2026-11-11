package com.labrox.iam.repository;

import com.labrox.iam.domain.AuditTrailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrailEvent, Long> {

    @Query(value = "SELECT * FROM iam_schema.audit_trail_events WHERE company_id = :companyId AND entity_name = :entityName AND entity_id = :entityId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<AuditTrailEvent> findLatestByEntity(
            @Param("companyId") Integer companyId,
            @Param("entityName") String entityName,
            @Param("entityId") String entityId);

    List<AuditTrailEvent> findByCompanyIdAndEntityNameAndEntityIdOrderByIdAsc(
            Integer companyId, String entityName, String entityId);
}

