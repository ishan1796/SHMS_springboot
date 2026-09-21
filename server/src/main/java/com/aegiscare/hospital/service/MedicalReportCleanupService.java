package com.aegiscare.hospital.service;

import com.aegiscare.hospital.repository.LabResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MedicalReportCleanupService {

    private static final Logger log = LoggerFactory.getLogger(MedicalReportCleanupService.class);

    private final LabResultRepository labResultRepository;

    public MedicalReportCleanupService(LabResultRepository labResultRepository) {
        this.labResultRepository = labResultRepository;
    }

    /**
     * Scheduled cleanup job: runs daily at 02:00 AM.
     * Computes the cutoff date (LocalDateTime.now().minusMonths(3)) using Java time calculation
     * and deletes medical/laboratory reports older than 3 months.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public int cleanupExpiredMedicalReports() {
        // Calculate 3 months before current date/time using Java LocalDateTime
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(3);
        log.info("Starting automated medical report cleanup for reports verified before: {}", cutoffDate);

        int deletedCount = labResultRepository.deleteByVerifiedAtBefore(cutoffDate);
        log.info("Completed medical report cleanup. Deleted {} expired report(s).", deletedCount);
        return deletedCount;
    }

    /**
     * On-demand cleanup method with custom months or default 3 months.
     */
    @Transactional
    public int cleanupReportsOlderThanMonths(int months) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(months);
        int deletedCount = labResultRepository.deleteByVerifiedAtBefore(cutoffDate);
        log.info("On-demand medical report cleanup for older than {} months (before {}): deleted {} report(s)",
                months, cutoffDate, deletedCount);
        return deletedCount;
    }
}
