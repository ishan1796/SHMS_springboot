package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    Optional<Doctor> findByUserId(String userId);
    Optional<Doctor> findByEmployeeId(String employeeId);
}
