package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, String> {
    List<MedicineBatch> findByMedicineId(String medicineId);
}
