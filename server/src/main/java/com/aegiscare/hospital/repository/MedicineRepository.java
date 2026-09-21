package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {
    List<Medicine> findByStockQuantityLessThanEqual(Integer threshold);
}
