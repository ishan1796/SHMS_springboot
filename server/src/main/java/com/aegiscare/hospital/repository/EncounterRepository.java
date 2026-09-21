package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, String> {
    List<Encounter> findByPatientIdOrderByCreatedAtDesc(String patientId);
    List<Encounter> findByDoctorIdOrderByCreatedAtDesc(String doctorId);
    List<Encounter> findByPatientId(String patientId);
    List<Encounter> findByDoctorId(String doctorId);
}