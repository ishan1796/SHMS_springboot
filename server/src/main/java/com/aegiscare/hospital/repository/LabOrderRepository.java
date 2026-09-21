package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, String> {
    List<LabOrder> findByPatientIdOrderByOrderDateDesc(String patientId);
    List<LabOrder> findByDoctorIdOrderByOrderDateDesc(String doctorId);
    List<LabOrder> findByStatusOrderByOrderDateDesc(String status);
    long countByStatus(String status);
}
