package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, String> {
    List<LabResult> findByLabOrderId(String labOrderId);
}
