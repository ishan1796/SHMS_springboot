package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, String> {
    Optional<Nurse> findByUserId(String userId);
    Optional<Nurse> findByEmployeeId(String employeeId);
}
