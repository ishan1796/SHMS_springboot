package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.NursingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NursingNoteRepository extends JpaRepository<NursingNote, String> {
    List<NursingNote> findByPatientIdOrderByCreatedAtDesc(String patientId);
    List<NursingNote> findByNurseId(String nurseId);
}
