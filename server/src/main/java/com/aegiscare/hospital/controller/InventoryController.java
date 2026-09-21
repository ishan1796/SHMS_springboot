package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.InventoryItem;
import com.aegiscare.hospital.entity.OxygenCylinder;
import com.aegiscare.hospital.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getItems() {
        List<InventoryItem> list = inventoryService.getItems();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("items", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/oxygen")
    public ResponseEntity<Map<String, Object>> getOxygen() {
        List<OxygenCylinder> list = inventoryService.getOxygenCylinders();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("cylinders", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/stock/add")
    public ResponseEntity<Map<String, Object>> addStock(@RequestBody Map<String, Object> body) {
        InventoryItem item = inventoryService.addStock(body);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("item", item);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/oxygen/{id}")
    public ResponseEntity<Map<String, Object>> updateOxygen(@PathVariable String id, @RequestBody Map<String, Object> body) {
        OxygenCylinder cyl = inventoryService.updateOxygen(id, body);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("cylinder", cyl);
        return ResponseEntity.ok(res);
    }
}