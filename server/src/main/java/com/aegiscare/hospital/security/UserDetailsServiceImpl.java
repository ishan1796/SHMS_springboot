package com.aegiscare.hospital.security;

import com.aegiscare.hospital.entity.User;
import com.aegiscare.hospital.repository.DoctorRepository;
import com.aegiscare.hospital.repository.EmployeeRepository;
import com.aegiscare.hospital.repository.PatientRepository;
import com.aegiscare.hospital.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EmployeeRepository employeeRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String patientId = patientRepository.findByUserId(user.getId()).map(p -> p.getId()).orElse(null);
        String doctorId = doctorRepository.findByUserId(user.getId()).map(d -> d.getId()).orElse(null);
        String employeeId = employeeRepository.findByUserId(user.getId()).map(e -> e.getId()).orElse(null);

        return new CustomUserDetails(user, patientId, doctorId, employeeId);
    }
}
