package com.labrox.iam.repository;

import com.labrox.iam.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByCompanyIdAndUsernameAndActiveTrue(Integer companyId, String username);
    Optional<Employee> findByUsernameAndActiveTrue(String username);
    Optional<Employee> findByUsernameIgnoreCaseAndActiveTrue(String username);
    Optional<Employee> findByCompanyIdAndUsernameIgnoreCaseAndActiveTrue(Integer companyId, String username);
    Optional<Employee> findByEmployeeCodeIgnoreCaseAndActiveTrue(String employeeCode);
    Optional<Employee> findByCompanyIdAndEmployeeCodeAndActiveTrue(Integer companyId, String employeeCode);
}

