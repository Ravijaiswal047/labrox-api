package com.labrox.sample.repository;

import com.labrox.sample.domain.SampleRejectionReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SampleRejectionReasonRepository extends JpaRepository<SampleRejectionReason, Integer> {
    List<SampleRejectionReason> findByActiveTrue();
}
