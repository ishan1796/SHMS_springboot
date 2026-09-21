package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.PharmacyDispensing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PharmacyDispensingRepository extends JpaRepository<PharmacyDispensing, String> {
    List<PharmacyDispensing> findByPatientIdOrderByDispensedAtDesc(String patientId);
}
