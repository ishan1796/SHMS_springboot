package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Patient;
import com.aegiscare.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPatients(@RequestParam(required = false) String search) {
        List<Patient> list = patientService.getAllPatients(search);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("patients", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPatientById(@PathVariable String id) {
        Map<String, Object> profile = patientService.getPatientProfile(id);
        return ResponseEntity.ok(profile);
    }
}