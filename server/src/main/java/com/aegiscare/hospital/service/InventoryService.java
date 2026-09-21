package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.InventoryItem;
import com.aegiscare.hospital.entity.OxygenCylinder;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.InventoryItemRepository;
import com.aegiscare.hospital.repository.OxygenCylinderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InventoryService {

    @Autowired private InventoryItemRepository inventoryItemRepository;
    @Autowired private OxygenCylinderRepository oxygenCylinderRepository;

    public List<InventoryItem> getItems() {
        return inventoryItemRepository.findAll();
    }

    public List<OxygenCylinder> getOxygenCylinders() {
        return oxygenCylinderRepository.findAll();
    }

    public InventoryItem addStock(Map<String, Object> req) {
        String itemId = (String) req.get("inventoryItemId");
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + itemId));
        int quantity = req.get("quantity") instanceof Number ? ((Number) req.get("quantity")).intValue() : 0;
        item.setCurrentStock(item.getCurrentStock() + quantity);
        return inventoryItemRepository.save(item);
    }

    public OxygenCylinder updateOxygen(String id, Map<String, Object> req) {
        OxygenCylinder cyl = oxygenCylinderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oxygen cylinder not found: " + id));
        if (req.containsKey("status")) cyl.setStatus((String) req.get("status"));
        if (req.containsKey("pressureBar")) {
            cyl.setCurrentPressureBar(((Number) req.get("pressureBar")).doubleValue());
        }
        if (req.containsKey("location")) cyl.setCurrentLocation((String) req.get("location"));
        return oxygenCylinderRepository.save(cyl);
    }
}