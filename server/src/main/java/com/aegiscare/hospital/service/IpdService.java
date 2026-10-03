package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class IpdService {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;
    private final BedAssignmentRepository bedAssignmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public IpdService(WardRepository wardRepository,
                      BedRepository bedRepository,
                      AdmissionRepository admissionRepository,
                      BedAssignmentRepository bedAssignmentRepository,
                      PatientRepository patientRepository,
                      DoctorRepository doctorRepository) {
        this.wardRepository = wardRepository;
        this.bedRepository = bedRepository;
        this.admissionRepository = admissionRepository;
        this.bedAssignmentRepository = bedAssignmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Ward> getWards() {
        return wardRepository.findAll();
    }

    public List<Bed> getBeds(String wardId) {
        if (wardId != null && !wardId.isBlank()) {
            return bedRepository.findByWardId(wardId);
        }
        return bedRepository.findAll();
    }

    public List<Admission> getAdmissions(String status) {
        if (status != null && !status.isBlank()) {
            return admissionRepository.findByStatus(status);
        }
        return admissionRepository.findAll();
    }

    public Admission admitPatient(Map<String, Object> req, String admittedBy) {
        String patientId = (String) req.get("patientId");
        String bedId = (String) req.get("bedId");
        String doctorId = (String) req.get("doctorId");
        String reasonForAdmission = (String) req.get("reasonForAdmission");
        if (reasonForAdmission == null || reasonForAdmission.isBlank()) {
            reasonForAdmission = (String) req.get("reason");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        // Enforce Doctor Admission Advice Rule: Only admitted if a Doctor has advised admission
        if (!patient.isAdmissionAdvised()) {
            throw new com.aegiscare.hospital.exception.ApiException(
                    "Admission Blocked: Patient " + patient.getFirstName() + " " + patient.getLastName() +
                    " (" + patient.getUhid() + ") cannot be admitted without prior Doctor Admission Advice recorded during clinical consultation."
            );
        }

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found: " + bedId));

        if ("OCCUPIED".equalsIgnoreCase(bed.getStatus())) {
            throw new com.aegiscare.hospital.exception.ApiException("Bed " + bed.getBedNumber() + " is currently OCCUPIED. Please select an AVAILABLE bed.");
        }

        Doctor doc = null;
        if (doctorId != null && !doctorId.isBlank()) {
            doc = doctorRepository.findById(doctorId).orElse(null);
        }
        if (doc == null) {
            List<Doctor> docs = doctorRepository.findAll();
            doc = docs.isEmpty() ? null : docs.get(0);
        }

        Admission admission = new Admission();
        admission.setAdmissionNumber("ADM-" + System.currentTimeMillis() % 1000000);
        admission.setPatient(patient);
        admission.setDoctor(doc);
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setReason(reasonForAdmission != null && !reasonForAdmission.isBlank()
                ? reasonForAdmission
                : (patient.getAdmissionAdviceNotes() != null ? patient.getAdmissionAdviceNotes() : "Inpatient therapeutic admission"));
        admission.setStatus("ACTIVE");
        admission.setWard(bed.getWard());
        admission.setBed(bed);

        Admission saved = admissionRepository.save(admission);

        // Update Bed Status
        bed.setStatus("OCCUPIED");
        bedRepository.save(bed);

        // Reset admission advised flag on patient since they are now admitted
        patient.setAdmissionAdvised(false);
        patientRepository.save(patient);

        BedAssignment assignment = new BedAssignment();
        assignment.setAdmission(saved);
        assignment.setBed(bed);
        assignment.setAssignedAt(LocalDateTime.now());
        bedAssignmentRepository.save(assignment);

        return saved;
    }

    public Admission dischargePatient(Map<String, Object> req, String dischargedBy) {
        String admissionId = (String) req.get("admissionId");
        Admission adm = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found: " + admissionId));

        adm.setStatus("DISCHARGED");
        adm.setDischargeDate(LocalDateTime.now());

        if (adm.getBed() != null) {
            Bed bed = adm.getBed();
            bed.setStatus("AVAILABLE");
            bedRepository.save(bed);
        }

        return admissionRepository.save(adm);
    }
}