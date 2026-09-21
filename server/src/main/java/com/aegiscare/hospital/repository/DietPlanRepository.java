package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, String> {
    List<DietPlan> findByPatientId(String patientId);
    List<DietPlan> findByStatus(String status);
    Optional<DietPlan> findByPatientIdAndStatus(String patientId, String status);
}