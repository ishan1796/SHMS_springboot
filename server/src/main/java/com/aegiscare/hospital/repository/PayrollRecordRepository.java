package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, String> {
    List<PayrollRecord> findByEmployeeIdOrderByYearDescMonthDesc(String employeeId);
    List<PayrollRecord> findByMonthAndYear(Integer month, Integer year);
}
