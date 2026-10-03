package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Attendance;
import com.aegiscare.hospital.entity.Department;
import com.aegiscare.hospital.entity.Employee;
import com.aegiscare.hospital.entity.LeaveRequest;
import com.aegiscare.hospital.entity.PayrollRecord;
import com.aegiscare.hospital.service.HrmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hrms")
public class HrmsController {

    @Autowired
    private HrmsService hrmsService;

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getEmployees() {
        List<Employee> list = hrmsService.getAllEmployees();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("employees", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/employees")
    public ResponseEntity<Map<String, Object>> createEmployee(@RequestBody Map<String, Object> body) {
        Employee emp = hrmsService.createEmployee(body);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("employee", emp);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable String id) {
        hrmsService.deleteEmployee(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Employee and associated hospital credentials deleted successfully.");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/departments")
    public ResponseEntity<Map<String, Object>> getDepartments() {
        List<Department> list = hrmsService.getAllDepartments();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("departments", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/leave")
    public ResponseEntity<Map<String, Object>> getLeaves(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String status) {
        List<LeaveRequest> list = hrmsService.getLeaveRequests(employeeId, status);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("leaves", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/leave")
    public ResponseEntity<Map<String, Object>> applyLeave(@RequestBody Map<String, Object> body, Authentication auth) {
        LeaveRequest req = hrmsService.applyLeave(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("leave", req);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/leave/{id}/status")
    public ResponseEntity<Map<String, Object>> updateLeaveStatus(
            @PathVariable String id,
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        String status = (String) body.get("status");
        String remarks = (String) body.get("remarks");
        LeaveRequest req = hrmsService.updateLeaveStatus(id, status, remarks, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("leave", req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/payroll")
    public ResponseEntity<Map<String, Object>> getPayroll(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        List<PayrollRecord> list = hrmsService.getPayrollRecords(month, year);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("payroll", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/payroll/generate")
    public ResponseEntity<Map<String, Object>> generatePayroll(@RequestBody Map<String, Object> body) {
        int month = body.get("month") instanceof Number ? ((Number) body.get("month")).intValue() : LocalDate.now().getMonthValue();
        int year = body.get("year") instanceof Number ? ((Number) body.get("year")).intValue() : LocalDate.now().getYear();
        List<PayrollRecord> records = hrmsService.generateMonthlyPayroll(month, year);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("payroll", records);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/attendance")
    public ResponseEntity<Map<String, Object>> getAttendance() {
        List<Attendance> list = hrmsService.getAttendance(LocalDate.now());
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("attendance", list);
        return ResponseEntity.ok(res);
    }
}