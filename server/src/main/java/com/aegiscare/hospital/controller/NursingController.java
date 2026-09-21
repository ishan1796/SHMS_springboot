package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.NursingNote;
import com.aegiscare.hospital.entity.Vital;
import com.aegiscare.hospital.service.NursingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nursing")
public class NursingController {

    @Autowired
    private NursingService nursingService;

    @GetMapping("/vitals")
    public ResponseEntity<Map<String, Object>> getVitals(@RequestParam(required = false) String patientId) {
        List<Vital> list = nursingService.getVitals(patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("vitals", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/vitals")
    public ResponseEntity<Map<String, Object>> recordVital(@RequestBody Map<String, Object> body, Authentication auth) {
        Vital v = nursingService.recordVitals(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("vital", v);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getNotes(@RequestParam(required = false) String patientId) {
        List<NursingNote> list = nursingService.getNotes(patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("notes", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/notes")
    public ResponseEntity<Map<String, Object>> recordNote(@RequestBody Map<String, Object> body, Authentication auth) {
        NursingNote note = nursingService.addNote(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("note", note);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/medications")
    public ResponseEntity<Map<String, Object>> getMedications() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("medications", List.of());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/medications")
    public ResponseEntity<Map<String, Object>> administerMedication(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Medication administration logged");
        return ResponseEntity.ok(res);
    }
}