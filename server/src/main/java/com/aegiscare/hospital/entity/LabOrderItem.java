package com.aegiscare.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "lab_order_items")
public class LabOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lab_order_id", nullable = false)
    private LabOrder labOrder;

    @ManyToOne
    @JoinColumn(name = "lab_test_id", nullable = false)
    private LabTest labTest;

    private String testName;
    private Double price = 0.0;

    public LabOrderItem() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LabOrder getLabOrder() { return labOrder; }
    public void setLabOrder(LabOrder labOrder) { this.labOrder = labOrder; }
    public LabTest getLabTest() { return labTest; }
    public void setLabTest(LabTest labTest) { this.labTest = labTest; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
