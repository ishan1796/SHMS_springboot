package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ApiException;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class PharmacyService {
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository medicineBatchRepository;
    private final PharmacyDispensingRepository dispensingRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PatientRepository patientRepository;
    private final ChargeRepository chargeRepository;

    public PharmacyService(MedicineRepository medicineRepository,
                           MedicineBatchRepository medicineBatchRepository,
                           PharmacyDispensingRepository dispensingRepository,
                           PrescriptionRepository prescriptionRepository,
                           PrescriptionItemRepository prescriptionItemRepository,
                           PatientRepository patientRepository,
                           ChargeRepository chargeRepository) {
        this.medicineRepository = medicineRepository;
        this.medicineBatchRepository = medicineBatchRepository;
        this.dispensingRepository = dispensingRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.patientRepository = patientRepository;
        this.chargeRepository = chargeRepository;
    }

    public List<Medicine> getMedicines() {
        return medicineRepository.findAll();
    }

    public List<MedicineBatch> getBatches(String medicineId) {
        if (medicineId != null) return medicineBatchRepository.findByMedicineId(medicineId);
        return medicineBatchRepository.findAll();
    }

    public List<PharmacyDispensing> getDispensings(String patientId) {
        if (patientId != null) return dispensingRepository.findByPatientIdOrderByDispensedAtDesc(patientId);
        return dispensingRepository.findAll();
    }

    @Transactional
    public PharmacyDispensing dispensePrescription(Map<String, Object> req) {
        String prescriptionId = (String) req.get("prescriptionId");
        String patientId = (String) req.get("patientId");

        Prescription rx = null;
        if (prescriptionId != null) {
            rx = prescriptionRepository.findById(prescriptionId).orElse(null);
            if (rx != null) {
                rx.setStatus("DISPENSED");
                prescriptionRepository.save(rx);
            }
        }

        Patient patient = rx != null ? rx.getPatient() : patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        PharmacyDispensing disp = new PharmacyDispensing();
        disp.setDispensingNumber("DSP-" + (System.currentTimeMillis() % 100000));
        disp.setPrescription(rx);
        disp.setPatient(patient);
        disp.setStatus("COMPLETED");

        List<Map<String, Object>> items = (List<Map<String, Object>>) req.get("items");
        double totalAmount = 0.0;

        if (items != null) {
            for (Map<String, Object> item : items) {
                String medId = (String) item.get("medicineId");
                int qty = item.get("quantity") != null ? ((Number) item.get("quantity")).intValue() : 1;
                Medicine med = medicineRepository.findById(medId)
                        .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + medId));

                if (med.getStockQuantity() < qty) {
                    throw new ApiException("Insufficient stock for " + med.getName() + " (Available: " + med.getStockQuantity() + ")");
                }

                med.setStockQuantity(med.getStockQuantity() - qty);
                medicineRepository.save(med);

                double price = med.getUnitPrice() != null ? med.getUnitPrice() : 10.0;
                totalAmount += (price * qty);

                PharmacyDispensingItem di = new PharmacyDispensingItem();
                di.setDispensing(disp);
                di.setMedicine(med);
                di.setQuantity(qty);
                di.setUnitPrice(price);
                di.setTotalPrice(price * qty);
                disp.getItems().add(di);
            }
        }
        disp.setTotalAmount(totalAmount);
        disp = dispensingRepository.save(disp);

        Charge charge = new Charge();
        charge.setPatient(patient);
        charge.setSourceModule("PHARMACY");
        charge.setSourceId(disp.getId());
        charge.setServiceName("Pharmacy Dispense #" + disp.getDispensingNumber());
        charge.setQuantity(1);
        charge.setUnitPrice(totalAmount);
        charge.setTotalAmount(totalAmount);
        charge.setStatus("UNBILLED");
        chargeRepository.save(charge);

        return disp;
    }
}
