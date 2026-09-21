package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Admission;
import com.aegiscare.hospital.entity.Bed;
import com.aegiscare.hospital.entity.Ward;
import com.aegiscare.hospital.service.IpdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ipd")
public class IpdController {

    @Autowired
    private IpdService ipdService;

    @GetMapping("/wards")
    public ResponseEntity<Map<String, Object>> getWards() {
        List<Ward> list = ipdService.getWards();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("wards", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/beds")
    public ResponseEntity<Map<String, Object>> getBeds(@RequestParam(required = false) String wardId) {
        List<Bed> list = ipdService.getBeds(wardId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("beds", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/admissions")
    public ResponseEntity<Map<String, Object>> getAdmissions(@RequestParam(required = false) String status) {
        List<Admission> list = ipdService.getAdmissions(status);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("admissions", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/admit")
    public ResponseEntity<Map<String, Object>> admitPatient(@RequestBody Map<String, Object> body, Authentication auth) {
        Admission adm = ipdService.admitPatient(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("admission", adm);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/discharge")
    public ResponseEntity<Map<String, Object>> dischargePatient(@RequestBody Map<String, Object> body, Authentication auth) {
        Admission adm = ipdService.dischargePatient(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("admission", adm);
        return ResponseEntity.ok(res);
    }
}