package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.AuditLog;
import com.aegiscare.hospital.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void record(String userId, String userEmail, String userRole, String action, String module, String targetId, String ipAddress, String details) {
        try {
            AuditLog log = new AuditLog(userId, userEmail, userRole, action, module, targetId, ipAddress, details);
            auditLogRepository.save(log);
        } catch (Exception ignored) {}
    }
}
