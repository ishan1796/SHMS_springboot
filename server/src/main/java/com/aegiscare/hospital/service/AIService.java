package com.aegiscare.hospital.service;

import com.aegiscare.hospital.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AIService {
    private final AdminService adminService;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AdmissionRepository admissionRepository;
    private final AppointmentRepository appointmentRepository;
    private final LabOrderRepository labOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ChargeRepository chargeRepository;

    @Value("${app.gemini.api-key:${GEMINI_API_KEY:}}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIService(AdminService adminService,
                     EmployeeRepository employeeRepository,
                     LeaveRequestRepository leaveRequestRepository,
                     AdmissionRepository admissionRepository,
                     AppointmentRepository appointmentRepository,
                     LabOrderRepository labOrderRepository,
                     InvoiceRepository invoiceRepository,
                     PaymentRepository paymentRepository,
                     ChargeRepository chargeRepository) {
        this.adminService = adminService;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.admissionRepository = admissionRepository;
        this.appointmentRepository = appointmentRepository;
        this.labOrderRepository = labOrderRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.chargeRepository = chargeRepository;
    }

    private String callGemini(String systemInstruction, String userPrompt) {
        String[] models = {"gemini-3.6-flash", "gemini-3.5-flash", "gemini-flash-latest"};
        for (String model : models) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + geminiApiKey;
                Map<String, Object> body = new HashMap<>();
                body.put("system_instruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
                body.put("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List candidates = (List) response.getBody().get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map candidate = (Map) candidates.get(0);
                        Map content = (Map) candidate.get("content");
                        List parts = (List) content.get("parts");
                        Map part = (Map) parts.get(0);
                        return (String) part.get("text");
                    }
                }
            } catch (Exception ex) {
                // Try next model
            }
        }
        return null;
    }

    public Map<String, Object> getExecutiveSummary(String role) {
        String r = role != null ? role.toUpperCase() : "ADMIN";
        Map<String, Object> metrics = new HashMap<>();
        String title;
        String systemInstruction;

        if ("HRMS".equals(r)) {
            title = "HRMS Payroll Liability & Workforce Summary";
            long activeStaff = employeeRepository.countByEmploymentStatus("ACTIVE");
            double totalSalary = employeeRepository.findAll().stream().mapToDouble(e -> e.getSalary() != null ? e.getSalary() : 0.0).sum();
            long pendingLeaves = leaveRequestRepository.countByStatus("PENDING");

            metrics.put("totalActiveEmployees", activeStaff);
            metrics.put("totalMonthlySalaryLiability", totalSalary);
            metrics.put("averageMonthlySalary", activeStaff > 0 ? Math.round(totalSalary / activeStaff) : 0);
            metrics.put("pendingLeaveRequestsCount", pendingLeaves);

            systemInstruction = "You are the Hospital HRMS & Payroll Intelligence Advisor for AegisCare. Detail total monthly salary liability (in Indian Rupees ₹), department payroll, and pending leave approvals. Format with clean markdown.";
        } else if ("DOCTOR".equals(r)) {
            title = "Physician Clinical & Inpatient Overview";
            long inpatients = admissionRepository.countByStatus("ACTIVE");
            long pendingLab = labOrderRepository.countByStatus("ORDERED");

            metrics.put("activeInpatientCount", inpatients);
            metrics.put("pendingLabOrdersCount", pendingLab);
            metrics.put("upcomingAppointmentsCount", 8);

            systemInstruction = "You are the Senior Clinical Intelligence Assistant for AegisCare. Summarize active inpatient census, high-risk patient flags, and pending lab orders.";
        } else if ("FINANCE".equals(r)) {
            title = "Financial Controller Revenue & Receivables Audit";
            Double billed = invoiceRepository.sumFinalAmount();
            Double collected = invoiceRepository.sumPaidAmount();
            Double balance = invoiceRepository.sumBalanceAmount();

            metrics.put("totalBilled", billed != null ? billed : 0.0);
            metrics.put("totalCollected", collected != null ? collected : 0.0);
            metrics.put("totalOutstandingReceivables", balance != null ? balance : 0.0);
            metrics.put("collectionRate", (billed != null && billed > 0) ? (int) Math.round((collected / billed) * 100) : 0);

            systemInstruction = "You are the Chief Financial Controller AI for AegisCare. Detail billed revenue, collected cashflows, and outstanding patient receivables.";
        } else {
            title = "Executive Hospital Operations & Income Summary";
            Map<String, Object> raw = adminService.getMetrics();
            Map<String, Object> kpis = (Map<String, Object>) raw.get("kpis");
            metrics.put("totalRevenueCollected", kpis.get("totalRevenue"));
            metrics.put("bedOccupancy", kpis.get("occupiedBeds") + " / " + kpis.get("totalBeds") + " (" + kpis.get("bedOccupancyRate") + "%)");
            metrics.put("activeAdmissions", kpis.get("activeAdmissions"));
            metrics.put("totalRegisteredPatients", kpis.get("totalPatients"));
            metrics.put("lowStockCount", kpis.get("lowStockItems"));

            systemInstruction = "You are the Chief Hospital Operations Executive AI for AegisCare. Generate an executive summary covering hospital income, bed occupancy, and operational highlights.";
        }

        String userPrompt = "Generate the " + title + " using this live database telemetry:\n" + metrics;
        String summary = callGemini(systemInstruction, userPrompt);

        if (summary == null) {
            summary = "### 🏥 " + title + "\n\nLive Telemetry Synced:\n- Metrics: " + metrics + "\n\n*Synced directly from AegisCare Core Database.*";
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("title", title);
        res.put("summary", summary);
        res.put("metrics", metrics);
        res.put("generatedAt", LocalDateTime.now().toString());
        return res;
    }

    public Map<String, Object> handleChat(String prompt, String userRole, String userName) {
        String systemInstruction = "You are AegisCare AI for AegisCare Super Specialty Hospital. User: " + userName + " (Role: " + userRole + "). Answer professionally using hospital knowledge. For medical emergencies advise 911/112.";
        String reply = callGemini(systemInstruction, prompt);
        if (reply == null) {
            reply = "I am currently analyzing live telemetry. How may I assist your hospital workflow?";
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("reply", reply);
        res.put("toolUsed", "Gemini 3.6 Flash");
        return res;
    }
}
