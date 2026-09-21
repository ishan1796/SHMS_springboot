package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, String> {
    Optional<SalaryStructure> findByEmployeeId(String employeeId);
}
