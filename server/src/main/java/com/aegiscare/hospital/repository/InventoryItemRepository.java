package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {
    List<InventoryItem> findByIsActiveTrue();
    List<InventoryItem> findByCurrentStockLessThanEqual(Integer threshold);
}