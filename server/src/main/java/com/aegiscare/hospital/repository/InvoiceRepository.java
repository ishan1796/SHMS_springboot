package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    List<Invoice> findByPatientIdOrderByCreatedAtDesc(String patientId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT SUM(i.finalAmount) FROM Invoice i")
    Double sumFinalAmount();

    @Query("SELECT SUM(i.paidAmount) FROM Invoice i")
    Double sumPaidAmount();

    @Query("SELECT SUM(i.balanceAmount) FROM Invoice i")
    Double sumBalanceAmount();
}
