package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, String> {
    List<Admission> findByStatus(String status);
    long countByStatus(String status);
    Optional<Admission> findByPatientIdAndStatus(String patientId, String status);
}
