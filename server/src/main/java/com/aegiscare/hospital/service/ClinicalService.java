package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ApiException;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private final LabOrderItemRepository labOrderItemRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final LabTestRepository labTestRepository;
    private final ChargeRepository chargeRepository;

    @Autowired
    public ClinicalService(AppointmentRepository appointmentRepository,
                           EncounterRepository encounterRepository,
                           PrescriptionRepository prescriptionRepository,
                           PrescriptionItemRepository prescriptionItemRepository,
                           DiagnosisRepository diagnosisRepository,
                           LabOrderRepository labOrderRepository,
                           LabOrderItemRepository labOrderItemRepository,
                           PatientRepository patientRepository,
                           DoctorRepository doctorRepository,
                           UserRepository userRepository,
                           MedicineRepository medicineRepository,
                           LabTestRepository labTestRepository,
                           ChargeRepository chargeRepository) {
        this.appointmentRepository = appointmentRepository;
        this.encounterRepository = encounterRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.labOrderRepository = labOrderRepository;
        this.labOrderItemRepository = labOrderItemRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.medicineRepository = medicineRepository;
        this.labTestRepository = labTestRepository;
        this.chargeRepository = chargeRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Appointment> getAppointments(String doctorId, String patientId) {
        if (doctorId != null && !doctorId.isBlank()) {
            return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
        }
        if (patientId != null && !patientId.isBlank()) {
            return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
        }
        return appointmentRepository.findAll();
    }

    /**
     * Generates all 10-minute doctor consultation time slots (e.g. 09:00 AM, 09:10 AM, 09:20 AM ...)
     * and identifies whether each slot is AVAILABLE or BOOKED for the chosen doctor and date.
     */
    public List<Map<String, Object>> getAvailableSlots(String doctorId, String dateStr) {
        LocalDate date = (dateStr != null && !dateStr.isBlank())
                ? LocalDate.parse(dateStr.substring(0, 10))
                : LocalDate.now();

        // 10-minute interval slots from 09:00 AM to 01:00 PM and 02:00 PM to 06:00 PM
        List<String> allSlots = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        // Morning Session: 09:00 AM to 01:00 PM
        LocalTime time = LocalTime.of(9, 0);
        LocalTime morningEnd = LocalTime.of(13, 0);
        while (time.isBefore(morningEnd)) {
            allSlots.add(time.format(timeFormatter));
            time = time.plusMinutes(10);
        }

        // Afternoon / Evening Session: 02:00 PM to 06:00 PM
        time = LocalTime.of(14, 0);
        LocalTime eveningEnd = LocalTime.of(18, 0);
        while (time.isBefore(eveningEnd)) {
            allSlots.add(time.format(timeFormatter));
            time = time.plusMinutes(10);
        }

        // Find already booked slots for this doctor on this date
        Set<String> bookedSlots = new HashSet<>();
        if (doctorId != null && !doctorId.isBlank()) {
            List<Appointment> existingAppts = appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, "CANCELLED");
            for (Appointment appt : existingAppts) {
                if (appt.getTimeSlot() != null) {
                    bookedSlots.add(appt.getTimeSlot().trim().toUpperCase());
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String slot : allSlots) {
            Map<String, Object> map = new HashMap<>();
            map.put("timeSlot", slot);
            boolean isBooked = bookedSlots.contains(slot.trim().toUpperCase());
            map.put("isAvailable", !isBooked);
            map.put("status", isBooked ? "BOOKED" : "AVAILABLE");
            result.add(map);
        }

        return result;
    }

    public Appointment bookAppointment(Map<String, Object> req, String bookedBy) {
        String patientId = (String) req.get("patientId");
        String doctorId = (String) req.get("doctorId");
        String dateStr = (String) req.get("appointmentDate");
        String timeSlot = (String) req.get("timeSlot");
        String type = (String) req.getOrDefault("type", "OPD");
        String reason = (String) req.get("reason");

        Patient patient = null;
        if (patientId != null && !patientId.isBlank()) {
            patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        } else if (bookedBy != null) {
            Optional<User> uOpt = userRepository.findByEmail(bookedBy);
            if (uOpt.isPresent()) {
                patient = patientRepository.findByUserId(uOpt.get().getId()).orElse(null);
            }
        }

        if (patient == null) {
            List<Patient> patients = patientRepository.findAll();
            if (!patients.isEmpty()) {
                patient = patients.get(0);
            } else {
                throw new ApiException("No patient profile found. Please register or select a patient.");
            }
        }

        Doctor doctor = null;
        if (doctorId != null && !doctorId.isBlank()) {
            doctor = doctorRepository.findById(doctorId).orElse(null);
        } else {
            List<Doctor> docs = doctorRepository.findAll();
            if (!docs.isEmpty()) doctor = docs.get(0);
        }

        if (doctor == null) {
            throw new ApiException("No doctor found. Please select a valid doctor.");
        }

        LocalDate apptDate = (dateStr != null && !dateStr.isBlank())
                ? LocalDate.parse(dateStr.substring(0, 10))
                : LocalDate.now().plusDays(1);

        String chosenSlot = (timeSlot != null && !timeSlot.isBlank()) ? timeSlot.trim() : "09:00 AM";

        // Validate Slot Availability (Concurrency / Duplicate Prevention)
        boolean conflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(
                doctor.getId(), apptDate, chosenSlot, "CANCELLED"
        );
        if (conflict) {
            throw new ApiException("The time slot " + chosenSlot + " on " + apptDate + " is already booked for Dr. " +
                    (doctor.getUser() != null ? doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName() : "Selected Doctor") +
                    ". Please choose another 10-minute slot.");
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        if (doctor.getDepartment() != null) appt.setDepartment(doctor.getDepartment());
        appt.setAppointmentDate(apptDate);
        appt.setTimeSlot(chosenSlot);
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

    @SuppressWarnings("unchecked")
    public Encounter createEncounter(Map<String, Object> req, String doctorEmail) {
        String patientId = (String) req.get("patientId");
        String appointmentId = (String) req.get("appointmentId");
        String symptoms = (String) req.getOrDefault("symptoms", req.get("chiefComplaint"));
        String doctorNotes = (String) req.getOrDefault("notes", req.get("doctorNotes"));
        String vitalsSummary = (String) req.get("vitalsSummary");

        // Doctor Admission Advice
        Boolean adviseAdmission = Boolean.TRUE.equals(req.get("adviseAdmission"));
        String admissionNotes = (String) req.get("admissionNotes");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        Doctor doctor = null;
        if (req.get("doctorId") != null) {
            doctor = doctorRepository.findById((String) req.get("doctorId")).orElse(null);
        }
        if (doctor == null && doctorEmail != null) {
            Optional<User> u = userRepository.findByEmail(doctorEmail);
            if (u.isPresent()) {
                doctor = doctorRepository.findByUserId(u.get().getId()).orElse(null);
            }
        }
        if (doctor == null) {
            List<Doctor> docs = doctorRepository.findAll();
            doctor = docs.isEmpty() ? null : docs.get(0);
        }

        Appointment appt = null;
        if (appointmentId != null && !appointmentId.isBlank()) {
            Optional<Appointment> aOpt = appointmentRepository.findById(appointmentId);
            if (aOpt.isPresent()) {
                appt = aOpt.get();
                appt.setStatus("COMPLETED");
                appointmentRepository.save(appt);
            }
        }

        // 1. Create Encounter
        Encounter enc = new Encounter();
        enc.setPatient(patient);
        enc.setDoctor(doctor);
        enc.setAppointment(appt);
        enc.setCreatedAt(LocalDateTime.now());
        enc.setSymptoms(symptoms);
        enc.setNotes(doctorNotes);
        enc.setVitalsSummary(vitalsSummary);
        enc.setStatus("COMPLETED");
        Encounter savedEnc = encounterRepository.save(enc);

        // 2. Handle Doctor Admission Advice
        if (adviseAdmission) {
            patient.setAdmissionAdvised(true);
            patient.setAdmissionAdviceNotes(admissionNotes != null && !admissionNotes.isBlank()
                    ? admissionNotes
                    : "Admission advised by Dr. " + (doctor != null && doctor.getUser() != null ? doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName() : "Physician") + " during OPD consultation.");
            patient.setAdmissionAdvisedBy(doctor != null && doctor.getUser() != null
                    ? "Dr. " + doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName()
                    : "Attending Doctor");
            patient.setAdmissionAdvisedAt(LocalDateTime.now());
            patientRepository.save(patient);
        }

        // 3. Process Diagnoses
        List<Map<String, Object>> diagnosesList = (List<Map<String, Object>>) req.get("diagnoses");
        if (diagnosesList != null && !diagnosesList.isEmpty()) {
            for (Map<String, Object> dMap : diagnosesList) {
                Diagnosis d = new Diagnosis();
                d.setEncounter(savedEnc);
                d.setPatient(patient);
                d.setDoctor(doctor);
                d.setDiagnosisName((String) dMap.getOrDefault("diagnosisName", "Clinical Diagnosis"));
                d.setIcdCode((String) dMap.getOrDefault("icdCode", "Z00.0"));
                d.setType((String) dMap.getOrDefault("type", "FINAL"));
                d.setCreatedAt(LocalDateTime.now());
                diagnosisRepository.save(d);
            }
        }

        // 4. Process Prescriptions
        Map<String, Object> rxMap = (Map<String, Object>) req.get("prescriptions");
        if (rxMap != null) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) rxMap.get("items");
            if (items != null && !items.isEmpty()) {
                Prescription rx = new Prescription();
                rx.setEncounter(savedEnc);
                rx.setPatient(patient);
                rx.setDoctor(doctor);
                rx.setDate(LocalDate.now());
                rx.setInstructions((String) rxMap.getOrDefault("instructions", "Take medications after meals"));
                rx.setStatus("PENDING");
                rx.setCreatedAt(LocalDateTime.now());
                Prescription savedRx = prescriptionRepository.save(rx);

                for (Map<String, Object> itemMap : items) {
                    PrescriptionItem item = new PrescriptionItem();
                    item.setPrescription(savedRx);
                    item.setMedicineName((String) itemMap.get("medicineName"));
                    item.setDosage((String) itemMap.getOrDefault("dosage", "1 tab"));
                    item.setFrequency((String) itemMap.getOrDefault("frequency", "1-0-1"));
                    Object dur = itemMap.get("durationDays");
                    item.setDurationDays(dur instanceof Number ? ((Number) dur).intValue() : 5);
                    Object qty = itemMap.get("quantity");
                    item.setQuantity(qty instanceof Number ? ((Number) qty).intValue() : 10);
                    prescriptionItemRepository.save(item);
                }
            }
        }

        // 5. Process Diagnostic Lab Orders
        List<Map<String, Object>> labOrdersList = (List<Map<String, Object>>) req.get("labOrders");
        if (labOrdersList != null && !labOrdersList.isEmpty()) {
            LabOrder lo = new LabOrder();
            lo.setOrderNumber("ORD-" + System.currentTimeMillis() % 1000000);
            lo.setPatient(patient);
            lo.setDoctor(doctor);
            lo.setEncounter(savedEnc);
            lo.setOrderDate(LocalDateTime.now());
            lo.setStatus("ORDERED");
            lo.setClinicalNotes("Ordered during OPD Encounter #" + savedEnc.getId().substring(0, 8));
            LabOrder savedLo = labOrderRepository.save(lo);

            for (Map<String, Object> tMap : labOrdersList) {
                String testId = (String) tMap.get("labTestId");
                LabTest test = null;
                if (testId != null) {
                    test = labTestRepository.findById(testId).orElse(null);
                }
                LabOrderItem item = new LabOrderItem();
                item.setLabOrder(savedLo);
                item.setLabTest(test);
                item.setTestName((String) tMap.getOrDefault("testName", test != null ? test.getName() : "Diagnostic Investigation"));
                Object price = tMap.get("price");
                item.setPrice(price instanceof Number ? ((Number) price).doubleValue() : (test != null ? test.getPrice() : 500.0));
                labOrderItemRepository.save(item);
            }
        }

        // 6. Generate OPD Consultation Billing Charge
        Charge charge = new Charge();
        charge.setPatient(patient);
        charge.setSourceModule("OPD");
        charge.setSourceId(savedEnc.getId());
        charge.setServiceName("Doctor OPD Consultation - Dr. " + (doctor != null && doctor.getUser() != null ? doctor.getUser().getLastName() : "General"));
        charge.setQuantity(1);
        charge.setUnitPrice(doctor != null && doctor.getConsultationFee() != null ? doctor.getConsultationFee() : 500.0);
        charge.setTotalAmount(charge.getUnitPrice());
        charge.setStatus("UNBILLED");
        chargeRepository.save(charge);

        return savedEnc;
    }
}