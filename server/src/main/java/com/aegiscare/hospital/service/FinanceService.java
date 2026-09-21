package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ApiException;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FinanceService {
    private final ChargeRepository chargeRepository;
    private final InvoiceRepository invoiceRepository;
    private final DiscountRepository discountRepository;
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;

    public FinanceService(ChargeRepository chargeRepository,
                          InvoiceRepository invoiceRepository,
                          DiscountRepository discountRepository,
                          PaymentRepository paymentRepository,
                          PatientRepository patientRepository) {
        this.chargeRepository = chargeRepository;
        this.invoiceRepository = invoiceRepository;
        this.discountRepository = discountRepository;
        this.paymentRepository = paymentRepository;
        this.patientRepository = patientRepository;
    }

    public List<Charge> getUnbilledCharges(String patientId) {
        if (patientId != null) return chargeRepository.findByPatientIdAndStatus(patientId, "UNBILLED");
        return chargeRepository.findByStatus("UNBILLED");
    }

    public List<Invoice> getInvoices(String patientId) {
        if (patientId != null) return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return invoiceRepository.findAll();
    }

    @Transactional
    public Invoice createInvoice(Map<String, Object> req, String finalizedBy) {
        String patientId = (String) req.get("patientId");
        List<String> chargeIds = (List<String>) req.get("chargeIds");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        if (chargeIds == null || chargeIds.isEmpty()) {
            throw new ApiException("At least one charge must be selected to generate an invoice.");
        }

        List<Charge> charges = chargeRepository.findAllById(chargeIds);
        double subtotal = 0.0;

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + (System.currentTimeMillis() % 100000));
        invoice.setPatient(patient);
        invoice.setFinalizedBy(finalizedBy);
        invoice.setFinalizedAt(LocalDateTime.now());
        invoice.setNotes((String) req.get("notes"));

        for (Charge c : charges) {
            c.setStatus("INVOICED");
            chargeRepository.save(c);

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setCharge(c);
            item.setDescription(c.getServiceName());
            item.setQuantity(c.getQuantity());
            item.setUnitPrice(c.getUnitPrice());
            item.setAmount(c.getTotalAmount());
            invoice.getItems().add(item);
            subtotal += c.getTotalAmount();
        }

        invoice.setSubtotal(subtotal);
        invoice.setFinalAmount(subtotal);
        invoice.setBalanceAmount(subtotal);
        invoice.setPaidAmount(0.0);
        invoice.setStatus("PENDING");

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Discount applyDiscount(String invoiceId, Map<String, Object> req, String approvedBy) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        String discountType = (String) req.getOrDefault("discountType", "PERCENTAGE");
        double value = req.get("value") != null ? ((Number) req.get("value")).doubleValue() : 10.0;
        double discountAmount = "PERCENTAGE".equalsIgnoreCase(discountType) ? (invoice.getSubtotal() * (value / 100.0)) : value;

        Discount discount = new Discount();
        discount.setInvoice(invoice);
        discount.setDiscountType(discountType);
        discount.setValue(value);
        discount.setDiscountAmount(discountAmount);
        discount.setReason((String) req.get("reason"));
        discount.setApprovedBy(approvedBy);
        discountRepository.save(discount);

        invoice.setDiscountAmount(discountAmount);
        invoice.setFinalAmount(Math.max(0, invoice.getSubtotal() - discountAmount));
        invoice.setBalanceAmount(Math.max(0, invoice.getFinalAmount() - invoice.getPaidAmount()));
        invoiceRepository.save(invoice);

        return discount;
    }

    @Transactional
    public Payment recordPayment(Map<String, Object> req, String recordedBy) {
        String invoiceId = (String) req.get("invoiceId");
        double amount = ((Number) req.get("amount")).doubleValue();

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        Payment payment = new Payment();
        payment.setReceiptNumber("RCP-" + (System.currentTimeMillis() % 100000));
        payment.setInvoice(invoice);
        payment.setPatient(invoice.getPatient());
        payment.setAmount(amount);
        payment.setPaymentMethod((String) req.getOrDefault("paymentMethod", "CASH"));
        payment.setTransactionRef((String) req.get("transactionRef"));
        payment.setNotes((String) req.get("notes"));
        payment.setRecordedBy(recordedBy);
        payment = paymentRepository.save(payment);

        invoice.setPaidAmount(invoice.getPaidAmount() + amount);
        invoice.setBalanceAmount(Math.max(0, invoice.getFinalAmount() - invoice.getPaidAmount()));
        if (invoice.getBalanceAmount() <= 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIALLY_PAID");
        }
        invoiceRepository.save(invoice);

        return payment;
    }
}
