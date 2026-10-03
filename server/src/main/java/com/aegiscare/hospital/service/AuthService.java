package com.aegiscare.hospital.service;

import com.aegiscare.hospital.dto.AuthDto;
import com.aegiscare.hospital.entity.Patient;
import com.aegiscare.hospital.entity.Role;
import com.aegiscare.hospital.entity.User;
import com.aegiscare.hospital.exception.ApiException;
import com.aegiscare.hospital.repository.DoctorRepository;
import com.aegiscare.hospital.repository.EmployeeRepository;
import com.aegiscare.hospital.repository.PatientRepository;
import com.aegiscare.hospital.repository.UserRepository;
import com.aegiscare.hospital.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PatientRepository patientRepository,
                       DoctorRepository doctorRepository,
                       EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.auditService = auditService;
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        return login(request, "127.0.0.1");
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request, String ipAddress) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Invalid email or password.");
        }

        if (!user.isActive()) {
            throw new ApiException("Your account has been deactivated. Please contact an administrator.");
        }

        String patientId = patientRepository.findByUserId(user.getId()).map(p -> p.getId()).orElse(null);
        String doctorId = doctorRepository.findByUserId(user.getId()).map(d -> d.getId()).orElse(null);
        String employeeId = employeeRepository.findByUserId(user.getId()).map(e -> e.getId()).orElse(null);

        String token = tokenProvider.generateToken(user, patientId, doctorId, employeeId);

        auditService.record(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "LOGIN",
                "AUTH",
                user.getId(),
                ipAddress,
                "User successfully logged in"
        );

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("role", user.getRole().name());
        userMap.put("patientId", patientId);
        userMap.put("doctorId", doctorId);
        userMap.put("employeeId", employeeId);

        return new AuthDto.LoginResponse(token, userMap);
    }

    @Transactional
    public Map<String, Object> register(AuthDto.RegisterRequest request) {
        return register(request, "127.0.0.1");
    }

    @Transactional
    public Map<String, Object> register(AuthDto.RegisterRequest request, String ipAddress) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new ApiException("An account with this email address already exists.");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            role = Role.PATIENT;
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String patientId = null;
        if (role == Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setUhid("UHID-" + (System.currentTimeMillis() % 10000000));
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setEmail(request.getEmail());
            patient.setPhone(request.getPhone());
            patient.setGender(request.getGender() != null ? request.getGender() : "MALE");
            patient.setBloodGroup(request.getBloodGroup() != null ? request.getBloodGroup() : "O_POSITIVE");
            patient.setAddress(request.getAddress());
            if (request.getDob() != null && !request.getDob().isBlank()) {
                try {
                    patient.setDob(java.time.LocalDate.parse(request.getDob()));
                } catch (Exception ignored) {}
            }
            Patient savedPatient = patientRepository.save(patient);
            patientId = savedPatient.getId();
        }

        String token = tokenProvider.generateToken(savedUser, patientId, null, null);

        auditService.record(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "REGISTER",
                "AUTH",
                savedUser.getId(),
                ipAddress,
                "New user registered with role: " + role.name()
        );

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", savedUser.getId());
        userMap.put("email", savedUser.getEmail());
        userMap.put("firstName", savedUser.getFirstName());
        userMap.put("lastName", savedUser.getLastName());
        userMap.put("role", savedUser.getRole().name());
        userMap.put("patientId", patientId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("message", "User registered successfully");
        response.put("user", userMap);
        return response;
    }

    public Map<String, Object> getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        String patientId = patientRepository.findByUserId(user.getId()).map(p -> p.getId()).orElse(null);
        String doctorId = doctorRepository.findByUserId(user.getId()).map(d -> d.getId()).orElse(null);
        String employeeId = employeeRepository.findByUserId(user.getId()).map(e -> e.getId()).orElse(null);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("role", user.getRole().name());
        userMap.put("patientId", patientId);
        userMap.put("doctorId", doctorId);
        userMap.put("employeeId", employeeId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", userMap);
        return response;
    }
}