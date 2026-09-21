package com.aegiscare.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "pharmacy_dispensing_items")
public class PharmacyDispensingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "dispensing_id", nullable = false)
    private PharmacyDispensing dispensing;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    private Integer quantity = 1;
    private Double unitPrice = 0.0;
    private Double totalPrice = 0.0;

    public PharmacyDispensingItem() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PharmacyDispensing getDispensing() { return dispensing; }
    public void setDispensing(PharmacyDispensing dispensing) { this.dispensing = dispensing; }
    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
