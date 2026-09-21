package com.aegiscare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String genericName;
    private String category;
    private String form; // TABLET, CAPSULE, SYRUP, INJECTION
    private String strength;
    private Double unitPrice = 0.0;
    private Integer stockQuantity = 0;
    private Integer reorderLevel = 100;
    private boolean isPrescriptionRequired = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Medicine() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }
    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    public boolean isPrescriptionRequired() { return isPrescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { isPrescriptionRequired = prescriptionRequired; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
