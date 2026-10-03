package com.aegiscare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String uhid;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate dob;
    private String gender; // MALE, FEMALE, OTHER
    private String bloodGroup; // A_POSITIVE, B_POSITIVE, O_POSITIVE, etc.

    @Column(nullable = false)
    private String phone;

    private String email;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String allergies;
    private String medicalHistory;

    // Doctor Admission Advice Flag & Details
    private boolean admissionAdvised = false;
    private String admissionAdviceNotes;
    private String admissionAdvisedBy;
    private LocalDateTime admissionAdvisedAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Patient() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getUhid() { return uhid; }
    public void setUhid(String uhid) { this.uhid = uhid; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public boolean isAdmissionAdvised() { return admissionAdvised; }
    public void setAdmissionAdvised(boolean admissionAdvised) { this.admissionAdvised = admissionAdvised; }
    public String getAdmissionAdviceNotes() { return admissionAdviceNotes; }
    public void setAdmissionAdviceNotes(String admissionAdviceNotes) { this.admissionAdviceNotes = admissionAdviceNotes; }
    public String getAdmissionAdvisedBy() { return admissionAdvisedBy; }
    public void setAdmissionAdvisedBy(String admissionAdvisedBy) { this.admissionAdvisedBy = admissionAdvisedBy; }
    public LocalDateTime getAdmissionAdvisedAt() { return admissionAdvisedAt; }
    public void setAdmissionAdvisedAt(LocalDateTime admissionAdvisedAt) { this.admissionAdvisedAt = admissionAdvisedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
