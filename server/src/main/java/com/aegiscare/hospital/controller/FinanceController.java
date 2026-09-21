package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.Charge;
import com.aegiscare.hospital.entity.Discount;
import com.aegiscare.hospital.entity.Invoice;
import com.aegiscare.hospital.entity.Payment;
import com.aegiscare.hospital.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/charges")
    public ResponseEntity<Map<String, Object>> getCharges(@RequestParam(required = false) String patientId) {
        List<Charge> list = financeService.getUnbilledCharges(patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("charges", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/invoices")
    public ResponseEntity<Map<String, Object>> getInvoices(@RequestParam(required = false) String patientId) {
        List<Invoice> list = financeService.getInvoices(patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("invoices", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/invoices")
    public ResponseEntity<Map<String, Object>> createInvoice(@RequestBody Map<String, Object> body, Authentication auth) {
        Invoice inv = financeService.createInvoice(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("invoice", inv);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/invoices/discount")
    public ResponseEntity<Map<String, Object>> applyDiscount(@RequestBody Map<String, Object> body, Authentication auth) {
        String invoiceId = (String) body.get("invoiceId");
        Discount d = financeService.applyDiscount(invoiceId, body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("discount", d);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/payments")
    public ResponseEntity<Map<String, Object>> recordPayment(@RequestBody Map<String, Object> body, Authentication auth) {
        Payment p = financeService.recordPayment(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("payment", p);
        return ResponseEntity.ok(res);
    }
}