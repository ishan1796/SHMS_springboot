package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.User;
import com.aegiscare.hospital.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(adminService.getMetrics());
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> list = adminService.getAllUsers();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("users", list);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable String id) {
        User u = adminService.toggleUserStatus(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("user", u);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Map<String, Object>> getAuditLogs() {
        List<Map<String, Object>> logs = adminService.getAuditLogs();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("auditLogs", logs);
        return ResponseEntity.ok(res);
    }
}