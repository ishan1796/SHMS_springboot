package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class NursingService {
    private final VitalRepository vitalRepository;
    private final NursingNoteRepository nursingNoteRepository;
    private final PatientRepository patientRepository;
    private final NurseRepository nurseRepository;
    private final AdmissionRepository admissionRepository;

    public NursingService(VitalRepository vitalRepository,
                          NursingNoteRepository nursingNoteRepository,
                          PatientRepository patientRepository,
                          NurseRepository nurseRepository,
                          AdmissionRepository admissionRepository) {
        this.vitalRepository = vitalRepository;
        this.nursingNoteRepository = nursingNoteRepository;
        this.patientRepository = patientRepository;
        this.nurseRepository = nurseRepository;
        this.admissionRepository = admissionRepository;
    }

    public List<Vital> getVitals(String patientId) {
        if (patientId != null) return vitalRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
        return vitalRepository.findAll();
    }

    @Transactional
    public Vital recordVitals(Map<String, Object> req, String nurseId) {
        String patientId = (String) req.get("patientId");
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        Nurse nurse = nurseId != null ? nurseRepository.findById(nurseId).orElse(null) : null;

        Vital v = new Vital();
        v.setPatient(patient);
        v.setNurse(nurse);
        v.setBloodPressure((String) req.get("bloodPressure"));
        if (req.get("temperature") != null) v.setTemperature(((Number) req.get("temperature")).doubleValue());
        if (req.get("pulseRate") != null) v.setPulseRate(((Number) req.get("pulseRate")).intValue());
        if (req.get("spo2") != null) v.setSpo2(((Number) req.get("spo2")).intValue());
        if (req.get("respiratoryRate") != null) v.setRespiratoryRate(((Number) req.get("respiratoryRate")).intValue());
        if (req.get("weight") != null) v.setWeight(((Number) req.get("weight")).doubleValue());
        v.setNotes((String) req.get("notes"));
        return vitalRepository.save(v);
    }

    public List<NursingNote> getNotes(String patientId) {
        if (patientId != null) return nursingNoteRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return nursingNoteRepository.findAll();
    }

    @Transactional
    public NursingNote addNote(Map<String, Object> req, String nurseId) {
        String patientId = (String) req.get("patientId");
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseGet(() -> nurseRepository.findAll().stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("No nurse found")));

        NursingNote n = new NursingNote();
        n.setPatient(patient);
        n.setNurse(nurse);
        n.setNoteType((String) req.getOrDefault("noteType", "PROGRESS"));
        n.setContent((String) req.get("content"));
        return nursingNoteRepository.save(n);
    }
}
