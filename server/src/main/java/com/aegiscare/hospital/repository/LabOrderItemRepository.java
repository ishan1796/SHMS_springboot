package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.LabOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabOrderItemRepository extends JpaRepository<LabOrderItem, String> {
    List<LabOrderItem> findByLabOrderId(String labOrderId);
}
