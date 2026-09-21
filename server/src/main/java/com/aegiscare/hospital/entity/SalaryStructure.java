package com.aegiscare.hospital.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "salary_structures")
public class SalaryStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "employee_id", unique = true, nullable = false)
    private Employee employee;

    private Double basicSalary = 0.0;
    private Double hra = 0.0;
    private Double allowances = 0.0;
    private Double deductions = 0.0;
    private Double netSalary = 0.0;

    public SalaryStructure() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }
    public Double getHra() { return hra; }
    public void setHra(Double hra) { this.hra = hra; }
    public Double getAllowances() { return allowances; }
    public void setAllowances(Double allowances) { this.allowances = allowances; }
    public Double getDeductions() { return deductions; }
    public void setDeductions(Double deductions) { this.deductions = deductions; }
    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
}
