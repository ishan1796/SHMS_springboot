package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.BedAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BedAssignmentRepository extends JpaRepository<BedAssignment, String> {
    List<BedAssignment> findByAdmissionIdOrderByAssignedAtDesc(String admissionId);
}
