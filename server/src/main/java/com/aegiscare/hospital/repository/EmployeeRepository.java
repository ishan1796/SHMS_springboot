package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUserId(String userId);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    List<Employee> findByEmploymentStatus(String employmentStatus);
    long countByEmploymentStatus(String employmentStatus);
}
