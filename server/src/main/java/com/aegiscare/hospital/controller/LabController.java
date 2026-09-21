package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.LabOrder;
import com.aegiscare.hospital.entity.LabResult;
import com.aegiscare.hospital.entity.LabTest;
import com.aegiscare.hospital.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lab")
public class LabController {

    @Autowired
    private LabService labService;

    @GetMapping("/tests")
    public ResponseEntity<Map<String, Object>> getTests() {
        List<LabTest> list = labService.getTests();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("tests", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(@RequestParam(required = false) String status) {
        List<LabOrder> list = labService.getOrders(status);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("orders", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/orders/{id}/sample")
    public ResponseEntity<Map<String, Object>> collectSample(@PathVariable String id, @RequestBody Map<String, Object> body) {
        LabOrder order = labService.updateOrderStatus(id, "SAMPLE_COLLECTED");
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("order", order);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/orders/{id}/results")
    public ResponseEntity<Map<String, Object>> recordResult(
            @PathVariable String id,
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        LabResult lr = labService.addResult(id, body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("result", lr);
        return ResponseEntity.ok(res);
    }
}