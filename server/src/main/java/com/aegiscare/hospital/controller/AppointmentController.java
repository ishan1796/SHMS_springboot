package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Appointment;
import com.aegiscare.hospital.service.ClinicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private ClinicalService clinicalService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAppointments(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId) {
        List<Appointment> list = clinicalService.getAppointments(doctorId, patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("appointments", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        Appointment appt = clinicalService.bookAppointment(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("appointment", appt);
        return ResponseEntity.ok(res);
    }
}