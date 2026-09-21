package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Encounter;
import com.aegiscare.hospital.entity.Prescription;
import com.aegiscare.hospital.service.ClinicalService;
import com.aegiscare.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clinical")
public class ClinicalController {

    @Autowired
    private ClinicalService clinicalService;

    @Autowired
    private PatientService patientService;

    @GetMapping("/encounters")
    public ResponseEntity<Map<String, Object>> getEncounters(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId) {
        List<Encounter> list = clinicalService.getEncounters(doctorId, patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("encounters", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/encounters")
    public ResponseEntity<Map<String, Object>> createEncounter(
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        Encounter enc = clinicalService.createEncounter(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("encounter", enc);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/history/{patientId}")
    public ResponseEntity<Map<String, Object>> getHistory(@PathVariable String patientId) {
        Map<String, Object> profile = patientService.getPatientProfile(patientId);
        return ResponseEntity.ok(profile);
    }
}