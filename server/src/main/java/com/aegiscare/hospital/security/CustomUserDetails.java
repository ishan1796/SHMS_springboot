package com.aegiscare.hospital.security;

import com.aegiscare.hospital.entity.Role;
import com.aegiscare.hospital.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;
    private final String patientId;
    private final String doctorId;
    private final String employeeId;

    public CustomUserDetails(User user, String patientId, String doctorId, String employeeId) {
        this.user = user;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.employeeId = employeeId;
    }

    public User getUser() { return user; }
    public String getId() { return user.getId(); }
    public Role getRole() { return user.getRole(); }
    public String getFirstName() { return user.getFirstName(); }
    public String getLastName() { return user.getLastName(); }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getEmployeeId() { return employeeId; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() { return user.getPasswordHash(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return user.isActive(); }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.isActive(); }
}
