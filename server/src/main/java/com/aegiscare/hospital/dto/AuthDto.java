package com.aegiscare.hospital.dto;

import com.aegiscare.hospital.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class AuthDto {

    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private boolean success = true;
        private String token;
        private Map<String, Object> user;

        public LoginResponse(String token, Map<String, Object> user) {
            this.token = token;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getToken() { return token; }
        public Map<String, Object> getUser() { return user; }
    }

    public static class RegisterRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        private String phone;
        private String role = "PATIENT";
        private String dob;
        private String gender;
        private String bloodGroup;
        private String address;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDob() { return dob; }
        public void setDob(String dob) { this.dob = dob; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getBloodGroup() { return bloodGroup; }
        public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}