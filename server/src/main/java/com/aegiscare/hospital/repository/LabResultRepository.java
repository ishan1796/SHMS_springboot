package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, String> {
    List<LabResult> findByLabOrderId(String labOrderId);

    @Modifying
    @Transactional
    @Query("DELETE FROM LabResult lr WHERE lr.verifiedAt < :cutoffDate")
    int deleteByVerifiedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}
