package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Medicine;
import com.aegiscare.hospital.entity.PharmacyDispensing;
import com.aegiscare.hospital.entity.Prescription;
import com.aegiscare.hospital.service.ClinicalService;
import com.aegiscare.hospital.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private ClinicalService clinicalService;

    @GetMapping("/medicines")
    public ResponseEntity<Map<String, Object>> getMedicines() {
        List<Medicine> list = pharmacyService.getMedicines();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("medicines", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<Map<String, Object>> getPrescriptions() {
        List<Prescription> list = clinicalService.getPrescriptions(null, null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("prescriptions", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/dispense")
    public ResponseEntity<Map<String, Object>> dispensePrescription(@RequestBody Map<String, Object> body) {
        PharmacyDispensing disp = pharmacyService.dispensePrescription(body);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("dispensing", disp);
        return ResponseEntity.ok(res);
    }
}