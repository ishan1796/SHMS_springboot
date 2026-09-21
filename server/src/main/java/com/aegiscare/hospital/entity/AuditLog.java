package com.aegiscare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;
    private String userEmail;
    private String userRole;
    private String action; // LOGIN, LOGOUT, CREATE, UPDATE, DELETE, DISPENSE, ADMIT
    private String module; // AUTH, OPD, IPD, PHARMACY, LAB, FINANCE, HRMS, DIET, AI
    private String targetId;
    private String ipAddress;
    @Column(columnDefinition = "TEXT")
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog() {}
    public AuditLog(String userId, String userEmail, String userRole, String action, String module, String targetId, String ipAddress, String details) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.action = action;
        this.module = module;
        this.targetId = targetId;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
