package com.labrox.catalog.repository;

import com.labrox.catalog.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    List<Doctor> findByCompanyIdAndActiveTrue(Integer companyId);
    Optional<Doctor> findByCompanyIdAndDoctorCodeAndActiveTrue(Integer companyId, String doctorCode);
    List<Doctor> findByCompanyIdAndDoctorNameContainingIgnoreCaseAndActiveTrue(Integer companyId, String query);
}
