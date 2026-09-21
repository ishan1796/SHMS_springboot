package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ClinicalService {

    private final AppointmentRepository appointmentRepository;
    private final EncounterRepository encounterRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final LabOrderRepository labOrderRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicineRepository;
    private final LabTestRepository labTestRepository;

    @Autowired
    public ClinicalService(AppointmentRepository appointmentRepository,
                           EncounterRepository encounterRepository,
                           PrescriptionRepository prescriptionRepository,
                           PrescriptionItemRepository prescriptionItemRepository,
                           DiagnosisRepository diagnosisRepository,
                           LabOrderRepository labOrderRepository,
                           PatientRepository patientRepository,
                           DoctorRepository doctorRepository,
                           MedicineRepository medicineRepository,
                           LabTestRepository labTestRepository) {
        this.appointmentRepository = appointmentRepository;
        this.encounterRepository = encounterRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.labOrderRepository = labOrderRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.medicineRepository = medicineRepository;
        this.labTestRepository = labTestRepository;
    }

    public List<Appointment> getAppointments(String doctorId, String patientId) {
        if (doctorId != null && !doctorId.isBlank()) {
            return appointmentRepository.findByDoctorId(doctorId);
        }
        if (patientId != null && !patientId.isBlank()) {
            return appointmentRepository.findByPatientId(patientId);
        }
        return appointmentRepository.findAll();
    }

    public Appointment bookAppointment(Map<String, Object> req, String bookedBy) {
        String patientId = (String) req.get("patientId");
        String doctorId = (String) req.get("doctorId");
        String dateStr = (String) req.get("appointmentDate");
        String timeSlot = (String) req.get("timeSlot");
        String type = (String) req.getOrDefault("type", "OPD");
        String reason = (String) req.get("reason");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        Doctor doctor = null;
        if (doctorId != null && !doctorId.isBlank()) {
            doctor = doctorRepository.findById(doctorId).orElse(null);
        } else {
            List<Doctor> docs = doctorRepository.findAll();
            if (!docs.isEmpty()) doctor = docs.get(0);
        }

        LocalDate apptDate = dateStr != null ? LocalDate.parse(dateStr.substring(0, 10)) : LocalDate.now().plusDays(1);

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        if (doctor != null) appt.setDepartment(doctor.getDepartment());
        appt.setAppointmentDate(apptDate);
        appt.setTimeSlot(timeSlot != null ? timeSlot : "10:00 AM");
        appt.setType(type);
        appt.setStatus("SCHEDULED");
        appt.setReason(reason);
        appt.setTokenNumber((int) (appointmentRepository.count() + 1));

        return appointmentRepository.save(appt);
    }

    public List<Encounter> getEncounters(String doctorId, String patientId) {
        if (patientId != null && !patientId.isBlank()) {
            return encounterRepository.findByPatientId(patientId);
        }
        if (doctorId != null && !doctorId.isBlank()) {
            return encounterRepository.findByDoctorId(doctorId);
        }
        return encounterRepository.findAll();
    }

    public List<Prescription> getPrescriptions(String doctorId, String patientId) {
        if (patientId != null && !patientId.isBlank()) {
            return prescriptionRepository.findByPatientId(patientId);
        }
        if (doctorId != null && !doctorId.isBlank()) {
            return prescriptionRepository.findByDoctorId(doctorId);
        }
        return prescriptionRepository.findAll();
    }

    public Encounter createEncounter(Map<String, Object> req, String doctorEmail) {
        String patientId = (String) req.get("patientId");
        String symptoms = (String) req.get("chiefComplaint");
        String doctorNotes = (String) req.get("doctorNotes");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        List<Doctor> docs = doctorRepository.findAll();
        Doctor doctor = docs.isEmpty() ? null : docs.get(0);

        Encounter enc = new Encounter();
        enc.setPatient(patient);
        enc.setDoctor(doctor);
        enc.setCreatedAt(LocalDateTime.now());
        enc.setSymptoms(symptoms);
        enc.setNotes(doctorNotes);
        enc.setStatus("COMPLETED");

        return encounterRepository.save(enc);
    }
}