package com.aegiscare.hospital.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "oxygen_cylinders")
public class OxygenCylinder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String cylinderCode;

    private String type = "TYPE_D"; // TYPE_B, TYPE_D, JUMBO
    private Integer capacityLiters = 1500;
    private Double currentPressureBar = 150.0;
    private String status = "AVAILABLE"; // AVAILABLE, IN_USE, EMPTY, REFILLING
    private String currentLocation = "CENTRAL_STORE";

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    public OxygenCylinder() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCylinderCode() { return cylinderCode; }
    public void setCylinderCode(String cylinderCode) { this.cylinderCode = cylinderCode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getCapacityLiters() { return capacityLiters; }
    public void setCapacityLiters(Integer capacityLiters) { this.capacityLiters = capacityLiters; }
    public Double getCurrentPressureBar() { return currentPressureBar; }
    public void setCurrentPressureBar(Double currentPressureBar) { this.currentPressureBar = currentPressureBar; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public Ward getWard() { return ward; }
    public void setWard(Ward ward) { this.ward = ward; }
}
