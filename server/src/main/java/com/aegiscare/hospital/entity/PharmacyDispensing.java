package com.aegiscare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pharmacy_dispensings")
public class PharmacyDispensing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String dispensingNumber;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private Double totalAmount = 0.0;
    private String status = "COMPLETED"; // PENDING, COMPLETED, CANCELLED
    private LocalDateTime dispensedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "dispensing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PharmacyDispensingItem> items = new ArrayList<>();

    public PharmacyDispensing() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDispensingNumber() { return dispensingNumber; }
    public void setDispensingNumber(String dispensingNumber) { this.dispensingNumber = dispensingNumber; }
    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDispensedAt() { return dispensedAt; }
    public void setDispensedAt(LocalDateTime dispensedAt) { this.dispensedAt = dispensedAt; }
    public List<PharmacyDispensingItem> getItems() { return items; }
    public void setItems(List<PharmacyDispensingItem> items) { this.items = items; }
}
