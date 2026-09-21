package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByPatientIdOrderByCreatedAtDesc(String patientId);
    List<Payment> findByInvoiceId(String invoiceId);

    @Query("SELECT SUM(p.amount) FROM Payment p")
    Double sumTotalPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.createdAt >= :startDate")
    Double sumPaymentsSince(LocalDateTime startDate);
}
