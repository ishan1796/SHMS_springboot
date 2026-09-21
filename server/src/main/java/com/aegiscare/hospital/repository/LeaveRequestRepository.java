package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);
}
