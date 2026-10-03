package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ApiException;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class HrmsService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final EncounterRepository encounterRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NursingNoteRepository nursingNoteRepository;

    public HrmsService(EmployeeRepository employeeRepository,
                       UserRepository userRepository,
                       DepartmentRepository departmentRepository,
                       DoctorRepository doctorRepository,
                       NurseRepository nurseRepository,
                       LeaveRequestRepository leaveRequestRepository,
                       AttendanceRepository attendanceRepository,
                       PayrollRecordRepository payrollRecordRepository,
                       AppointmentRepository appointmentRepository,
                       EncounterRepository encounterRepository,
                       PrescriptionRepository prescriptionRepository,
                       NursingNoteRepository nursingNoteRepository,
                       PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollRecordRepository = payrollRecordRepository;
        this.appointmentRepository = appointmentRepository;
        this.encounterRepository = encounterRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.nursingNoteRepository = nursingNoteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<LeaveRequest> getLeaveRequests(String employeeId, String status) {
        if (employeeId != null) return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
        if (status != null) return leaveRequestRepository.findByStatusOrderByCreatedAtDesc(status);
        return leaveRequestRepository.findAll();
    }

    public List<Attendance> getAttendance(LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return attendanceRepository.findByDate(d);
    }

    public List<PayrollRecord> getPayrollRecords(Integer month, Integer year) {
        if (month != null && year != null) return payrollRecordRepository.findByMonthAndYear(month, year);
        return payrollRecordRepository.findAll();
    }

    @Transactional
    public Employee createEmployee(Map<String, Object> req) {
        String email = ((String) req.get("email")).toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new ApiException("User already exists with email: " + email);
        }

        Role role = Role.valueOf((String) req.getOrDefault("role", "DOCTOR"));
        String deptId = (String) req.get("departmentId");
        Department dept = deptId != null ? departmentRepository.findById(deptId).orElse(null) : null;
        if (dept == null) {
            dept = departmentRepository.findAll().stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("No department available"));
        }

        User user = new User(
                email,
                passwordEncoder.encode((String) req.getOrDefault("password", "password123")),
                role,
                (String) req.get("firstName"),
                (String) req.get("lastName"),
                (String) req.get("phone")
        );
        user = userRepository.save(user);

        Employee emp = new Employee();
        emp.setUser(user);
        emp.setEmployeeCode("EMP-" + (System.currentTimeMillis() % 10000));
        emp.setDepartment(dept);
        emp.setDesignation((String) req.getOrDefault("designation", "Hospital Staff"));
        emp.setSalary(req.get("salary") != null ? ((Number) req.get("salary")).doubleValue() : 50000.0);
        emp.setShift((String) req.getOrDefault("shift", "GENERAL"));
        emp = employeeRepository.save(emp);

        if (role == Role.DOCTOR) {
            Doctor doc = new Doctor();
            doc.setUser(user);
            doc.setEmployee(emp);
            doc.setDepartment(dept);
            doc.setSpecialization((String) req.getOrDefault("specialization", "General Medicine"));
            doc.setLicenseNumber((String) req.getOrDefault("licenseNumber", "MCI-" + (System.currentTimeMillis() % 100000)));
            doc.setConsultationFee(req.get("consultationFee") != null ? ((Number) req.get("consultationFee")).doubleValue() : 800.0);
            doc.setRoomNumber((String) req.getOrDefault("roomNumber", "OPD-101"));
            doctorRepository.save(doc);
        } else if (role == Role.NURSE) {
            Nurse nurse = new Nurse();
            nurse.setUser(user);
            nurse.setEmployee(emp);
            nurse.setDepartment(dept);
            nurse.setLicenseNumber((String) req.getOrDefault("licenseNumber", "INC-" + (System.currentTimeMillis() % 100000)));
            nurseRepository.save(nurse);
        }

        return emp;
    }

    @Transactional
    public LeaveRequest applyLeave(Map<String, Object> req, String employeeId) {
        Employee emp = employeeId != null ? employeeRepository.findById(employeeId).orElse(null) : null;
        if (emp == null) {
            String userId = (String) req.get("userId");
            if (userId != null) emp = employeeRepository.findByUserId(userId).orElse(null);
        }
        if (emp == null) {
            emp = employeeRepository.findAll().stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("No employee found"));
        }

        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(emp);
        lr.setLeaveType((String) req.getOrDefault("leaveType", "CASUAL"));
        lr.setStartDate(LocalDate.parse((String) req.get("startDate")));
        lr.setEndDate(LocalDate.parse((String) req.get("endDate")));
        lr.setReason((String) req.get("reason"));
        lr.setStatus("PENDING");
        return leaveRequestRepository.save(lr);
    }

    @Transactional
    public LeaveRequest updateLeaveStatus(String leaveId, String status, String remarks, String reviewedBy) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
        lr.setStatus(status);
        lr.setRemarks(remarks);
        lr.setReviewedBy(reviewedBy);
        return leaveRequestRepository.save(lr);
    }

    @Transactional
    public List<PayrollRecord> generateMonthlyPayroll(int month, int year) {
        List<Employee> employees = employeeRepository.findByEmploymentStatus("ACTIVE");
        List<PayrollRecord> records = new ArrayList<>();

        for (Employee emp : employees) {
            PayrollRecord pr = new PayrollRecord();
            pr.setEmployee(emp);
            pr.setMonth(month);
            pr.setYear(year);
            double base = emp.getSalary() != null ? emp.getSalary() : 40000.0;
            pr.setBasicSalary(base * 0.7);
            pr.setHra(base * 0.2);
            pr.setAllowances(base * 0.1);
            pr.setDeductions(base * 0.05);
            pr.setNetSalary(base * 0.95);
            pr.setStatus("DISBURSED");
            records.add(payrollRecordRepository.save(pr));
        }
        return records;
    }

    @Transactional
    public void deleteEmployee(String employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        User user = emp.getUser();

        // 1. Delete associated HRMS records
        leaveRequestRepository.deleteAll(leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId));
        attendanceRepository.deleteAll(attendanceRepository.findByEmployeeIdOrderByDateDesc(employeeId));
        payrollRecordRepository.deleteAll(payrollRecordRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId));

        // 2. Clean up Doctor entity if exists
        Optional<Doctor> docOpt = doctorRepository.findByEmployeeId(employeeId);
        if (docOpt.isPresent()) {
            Doctor doc = docOpt.get();
            // Reassign or clean appointments/encounters/prescriptions to prevent orphan foreign keys
            List<Doctor> otherDocs = doctorRepository.findAll().stream()
                    .filter(d -> !d.getId().equals(doc.getId()))
                    .toList();
            Doctor fallbackDoc = otherDocs.isEmpty() ? null : otherDocs.get(0);

            List<Appointment> appts = appointmentRepository.findByDoctorId(doc.getId());
            for (Appointment a : appts) {
                if (fallbackDoc != null) {
                    a.setDoctor(fallbackDoc);
                    appointmentRepository.save(a);
                } else {
                    appointmentRepository.delete(a);
                }
            }

            List<Encounter> encounters = encounterRepository.findByDoctorId(doc.getId());
            for (Encounter enc : encounters) {
                if (fallbackDoc != null) {
                    enc.setDoctor(fallbackDoc);
                    encounterRepository.save(enc);
                }
            }

            List<Prescription> prescriptions = prescriptionRepository.findByDoctorId(doc.getId());
            for (Prescription p : prescriptions) {
                if (fallbackDoc != null) {
                    p.setDoctor(fallbackDoc);
                    prescriptionRepository.save(p);
                }
            }

            doctorRepository.delete(doc);
        }

        // 3. Clean up Nurse entity if exists
        Optional<Nurse> nurseOpt = nurseRepository.findByEmployeeId(employeeId);
        if (nurseOpt.isPresent()) {
            Nurse nurse = nurseOpt.get();
            List<NursingNote> notes = nursingNoteRepository.findByNurseId(nurse.getId());
            nursingNoteRepository.deleteAll(notes);
            nurseRepository.delete(nurse);
        }

        // 4. Delete Employee record
        employeeRepository.delete(emp);

        // 5. Delete linked User login account
        if (user != null) {
            userRepository.delete(user);
        }
    }
}
