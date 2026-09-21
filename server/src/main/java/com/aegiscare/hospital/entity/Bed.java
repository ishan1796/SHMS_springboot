package com.aegiscare.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "beds")
public class Bed {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"beds"})
    @ManyToOne
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false)
    private String bedNumber;

    private String type = "STANDARD"; // STANDARD, ICU, OXYGEN, VENTILATOR
    private Double dailyRate = 1000.0;
    private String status = "AVAILABLE"; // AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED

    public Bed() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Ward getWard() { return ward; }
    public void setWard(Ward ward) { this.ward = ward; }
    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
