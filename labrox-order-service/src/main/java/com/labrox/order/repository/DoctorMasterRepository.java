package com.labrox.order.repository;

import com.labrox.order.domain.DoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, Integer> {
    List<DoctorMaster> findByCompanyIdAndActiveTrueOrderByDoctorName(Integer companyId);
}
