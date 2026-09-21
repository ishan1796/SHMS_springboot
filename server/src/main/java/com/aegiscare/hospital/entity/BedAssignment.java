package com.aegiscare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bed_assignments")
public class BedAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "admission_id", nullable = false)
    private Admission admission;

    @ManyToOne
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    private LocalDateTime assignedAt = LocalDateTime.now();
    private LocalDateTime vacatedAt;

    public BedAssignment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Admission getAdmission() { return admission; }
    public void setAdmission(Admission admission) { this.admission = admission; }
    public Bed getBed() { return bed; }
    public void setBed(Bed bed) { this.bed = bed; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public LocalDateTime getVacatedAt() { return vacatedAt; }
    public void setVacatedAt(LocalDateTime vacatedAt) { this.vacatedAt = vacatedAt; }
}
