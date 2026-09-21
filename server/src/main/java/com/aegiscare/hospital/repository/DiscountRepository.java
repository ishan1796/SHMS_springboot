package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    List<Discount> findByInvoiceId(String invoiceId);
}
