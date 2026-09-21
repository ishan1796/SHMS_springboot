package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, String> {
    Optional<LabTest> findByCode(String code);
    List<LabTest> findByIsActiveTrue();
}
