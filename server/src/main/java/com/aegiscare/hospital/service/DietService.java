package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.Admission;
import com.aegiscare.hospital.entity.DietPlan;
import com.aegiscare.hospital.entity.Doctor;
import com.aegiscare.hospital.entity.MealFulfillment;
import com.aegiscare.hospital.entity.Patient;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.AdmissionRepository;
import com.aegiscare.hospital.repository.DietPlanRepository;
import com.aegiscare.hospital.repository.DoctorRepository;
import com.aegiscare.hospital.repository.MealFulfillmentRepository;
import com.aegiscare.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class DietService {

    private final DietPlanRepository dietPlanRepository;
    private final MealFulfillmentRepository mealFulfillmentRepository;
    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public DietService(DietPlanRepository dietPlanRepository,
                       MealFulfillmentRepository mealFulfillmentRepository,
                       AdmissionRepository admissionRepository,
                       PatientRepository patientRepository,
                       DoctorRepository doctorRepository) {
        this.dietPlanRepository = dietPlanRepository;
        this.mealFulfillmentRepository = mealFulfillmentRepository;
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Map<String, Object>> getAdmittedPatientsForDiet() {
        List<Admission> admissions = admissionRepository.findByStatus("ADMITTED");
        List<Map<String, Object>> result = new ArrayList<>();

        for (Admission adm : admissions) {
            Map<String, Object> map = new HashMap<>();
            map.put("admissionId", adm.getId());
            map.put("patient", adm.getPatient());
            map.put("bed", adm.getBed());
            map.put("ward", adm.getWard());
            map.put("admissionDate", adm.getAdmissionDate());

            Optional<DietPlan> activePlan = dietPlanRepository.findByPatientIdAndStatus(adm.getPatient().getId(), "ACTIVE");
            map.put("activeDietPlan", activePlan.orElse(null));

            result.add(map);
        }
        return result;
    }

    public List<DietPlan> getAllDietPlans() {
        return dietPlanRepository.findAll();
    }

    public List<MealFulfillment> getMealQueue(LocalDate date) {
        return mealFulfillmentRepository.findByMealDate(date != null ? date : LocalDate.now());
    }

    public DietPlan createDietPlan(Map<String, Object> req, String doctorEmail) {
        String patientId = (String) req.get("patientId");
        String dietType = (String) req.get("dietType");
        String breakfast = (String) req.get("breakfast");
        String lunch = (String) req.get("lunch");
        String dinner = (String) req.get("dinner");
        String specialInstructions = (String) req.get("specialInstructions");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        List<Doctor> docs = doctorRepository.findAll();
        Doctor doc = docs.isEmpty() ? null : docs.get(0);

        DietPlan plan = new DietPlan();
        plan.setPatient(patient);
        plan.setDoctor(doc);
        plan.setDietType(dietType != null ? dietType : "NORMAL");
        plan.setBreakfast(breakfast);
        plan.setLunch(lunch);
        plan.setDinner(dinner);
        plan.setSpecialInstructions(specialInstructions);
        plan.setStatus("ACTIVE");

        return dietPlanRepository.save(plan);
    }

    public MealFulfillment updateMealStatus(String fulfillmentId, String status) {
        MealFulfillment mf = mealFulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal fulfillment record not found: " + fulfillmentId));
        mf.setStatus(status);
        if ("DELIVERED".equalsIgnoreCase(status)) {
            mf.setDeliveredAt(LocalDateTime.now());
        }
        return mealFulfillmentRepository.save(mf);
    }
}