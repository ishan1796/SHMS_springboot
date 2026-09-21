package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByEmployeeIdOrderByDateDesc(String employeeId);
    List<Attendance> findByDate(LocalDate date);
}
