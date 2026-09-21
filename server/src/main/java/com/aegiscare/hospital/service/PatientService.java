package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.Patient;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final EncounterRepository encounterRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabOrderRepository labOrderRepository;
    private final AdmissionRepository admissionRepository;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          EncounterRepository encounterRepository,
                          PrescriptionRepository prescriptionRepository,
                          LabOrderRepository labOrderRepository,
                          AdmissionRepository admissionRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.encounterRepository = encounterRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labOrderRepository = labOrderRepository;
        this.admissionRepository = admissionRepository;
    }

    public List<Patient> getAllPatients(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return patientRepository.searchPatients(search.trim());
        }
        return patientRepository.findAll();
    }

    public Map<String, Object> getPatientProfile(String patientId) {
        Patient p = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("patient", p);
        res.put("appointments", appointmentRepository.findByPatientId(patientId));
        res.put("encounters", encounterRepository.findByPatientId(patientId));
        res.put("prescriptions", prescriptionRepository.findByPatientId(patientId));
        res.put("labOrders", labOrderRepository.findByPatientIdOrderByOrderDateDesc(patientId));
        res.put("admissions", admissionRepository.findByPatientIdAndStatus(patientId, "ACTIVE").map(List::of).orElse(Collections.emptyList()));
        return res;
    }
}