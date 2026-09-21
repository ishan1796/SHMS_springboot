package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, String> {
    List<Diagnosis> findByEncounterId(String encounterId);
    List<Diagnosis> findByPatientId(String patientId);
}
