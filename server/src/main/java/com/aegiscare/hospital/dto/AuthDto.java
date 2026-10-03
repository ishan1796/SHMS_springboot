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
        private String firstName;
        private String lastName;
        private String name;
        private String fullName;
        private String username;
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

        public String getFirstName() {
            if (firstName != null && !firstName.isBlank()) {
                return firstName.trim();
            }
            String combined = (fullName != null && !fullName.isBlank()) ? fullName : (name != null && !name.isBlank()) ? name : username;
            if (combined != null && !combined.isBlank()) {
                String[] parts = combined.trim().split("\\s+");
                return parts[0];
            }
            if (email != null && email.contains("@")) {
                return email.substring(0, email.indexOf("@"));
            }
            return "User";
        }

        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() {
            if (lastName != null && !lastName.isBlank()) {
                return lastName.trim();
            }
            String combined = (fullName != null && !fullName.isBlank()) ? fullName : (name != null && !name.isBlank()) ? name : username;
            if (combined != null && !combined.isBlank()) {
                String[] parts = combined.trim().split("\\s+", 2);
                if (parts.length > 1) {
                    return parts[1];
                }
            }
            return "";
        }

        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPhone() { return phone != null ? phone : ""; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role != null ? role : "PATIENT"; }
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