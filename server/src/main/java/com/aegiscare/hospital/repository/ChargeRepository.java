package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, String> {
    List<Charge> findByPatientIdAndStatus(String patientId, String status);
    List<Charge> findByStatus(String status);

    @Query("SELECT c.sourceModule as module, SUM(c.totalAmount) as total FROM Charge c GROUP BY c.sourceModule")
    List<Map<String, Object>> getRevenueByDepartment();
}
