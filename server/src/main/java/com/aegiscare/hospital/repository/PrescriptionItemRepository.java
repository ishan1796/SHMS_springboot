package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, String> {
    List<PrescriptionItem> findByPrescriptionId(String prescriptionId);
}
