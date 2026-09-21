package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.User;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;
    private final AppointmentRepository appointmentRepository;
    private final LabOrderRepository labOrderRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    private final ChargeRepository chargeRepository;

    public AdminService(UserRepository userRepository,
                        PatientRepository patientRepository,
                        BedRepository bedRepository,
                        AdmissionRepository admissionRepository,
                        AppointmentRepository appointmentRepository,
                        LabOrderRepository labOrderRepository,
                        InventoryItemRepository inventoryItemRepository,
                        PaymentRepository paymentRepository,
                        AuditLogRepository auditLogRepository,
                        ChargeRepository chargeRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.bedRepository = bedRepository;
        this.admissionRepository = admissionRepository;
        this.appointmentRepository = appointmentRepository;
        this.labOrderRepository = labOrderRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.paymentRepository = paymentRepository;
        this.auditLogRepository = auditLogRepository;
        this.chargeRepository = chargeRepository;
    }

    public Map<String, Object> getMetrics() {
        long totalPatients = patientRepository.count();
        long activeAdmissions = admissionRepository.countByStatus("ACTIVE");
        long totalBeds = bedRepository.count();
        long occupiedBeds = bedRepository.countByStatus("OCCUPIED");
        int bedRate = totalBeds > 0 ? (int) Math.round(((double) occupiedBeds / totalBeds) * 100) : 0;
        long todayAppts = appointmentRepository.countByAppointmentDate(LocalDate.now());
        long pendingLab = labOrderRepository.countByStatus("ORDERED") + labOrderRepository.countByStatus("PROCESSING");
        long lowStock = inventoryItemRepository.findByCurrentStockLessThanEqual(20).size();

        Double totalRev = paymentRepository.sumTotalPayments();
        Double todayRev = paymentRepository.sumPaymentsSince(LocalDate.now().atStartOfDay());

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalPatients", totalPatients);
        kpis.put("activeAdmissions", activeAdmissions);
        kpis.put("totalBeds", totalBeds);
        kpis.put("occupiedBeds", occupiedBeds);
        kpis.put("bedOccupancyRate", bedRate);
        kpis.put("todayAppointments", todayAppts);
        kpis.put("pendingLabOrders", pendingLab);
        kpis.put("lowStockItems", lowStock);
        kpis.put("totalRevenue", totalRev != null ? totalRev : 0.0);
        kpis.put("todayRevenue", todayRev != null ? todayRev : 0.0);

        List<Map<String, Object>> apptTrends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", d.toString());
            item.put("appointments", appointmentRepository.countByAppointmentDate(d));
            apptTrends.add(item);
        }

        Map<String, Object> charts = new HashMap<>();
        charts.put("appointmentTrends", apptTrends);
        charts.put("departmentRevenue", chargeRepository.getRevenueByDepartment());

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("kpis", kpis);
        res.put("charts", charts);
        return res;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User toggleUserStatus(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    public List<Map<String, Object>> getAuditLogs() {
        List<Map<String, Object>> list = new ArrayList<>();
        auditLogRepository.findTop50ByOrderByTimestampDesc().forEach(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId());
            m.put("userId", l.getUserId());
            m.put("userEmail", l.getUserEmail());
            m.put("userRole", l.getUserRole());
            m.put("action", l.getAction());
            m.put("module", l.getModule());
            m.put("targetId", l.getTargetId());
            m.put("ipAddress", l.getIpAddress());
            m.put("details", l.getDetails());
            m.put("timestamp", l.getTimestamp().toString());
            list.add(m);
        });
        return list;
    }
}
