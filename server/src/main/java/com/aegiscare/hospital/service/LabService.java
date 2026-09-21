package com.aegiscare.hospital.service;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.exception.ResourceNotFoundException;
import com.aegiscare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class LabService {
    private final LabTestRepository labTestRepository;
    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final ChargeRepository chargeRepository;

    public LabService(LabTestRepository labTestRepository,
                      LabOrderRepository labOrderRepository,
                      LabResultRepository labResultRepository,
                      ChargeRepository chargeRepository) {
        this.labTestRepository = labTestRepository;
        this.labOrderRepository = labOrderRepository;
        this.labResultRepository = labResultRepository;
        this.chargeRepository = chargeRepository;
    }

    public List<LabTest> getTests() {
        return labTestRepository.findAll();
    }

    public List<LabOrder> getOrders(String status) {
        if (status != null) return labOrderRepository.findByStatusOrderByOrderDateDesc(status);
        return labOrderRepository.findAll();
    }

    public List<LabResult> getResults(String orderId) {
        return labResultRepository.findByLabOrderId(orderId);
    }

    @Transactional
    public LabOrder updateOrderStatus(String orderId, String status) {
        LabOrder lo = labOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        lo.setStatus(status);
        return labOrderRepository.save(lo);
    }

    @Transactional
    public LabResult addResult(String orderId, Map<String, Object> req, String verifiedBy) {
        LabOrder lo = labOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        String testId = (String) req.get("labTestId");
        LabTest test = labTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab test not found: " + testId));

        LabResult r = new LabResult();
        r.setLabOrder(lo);
        r.setLabTest(test);
        r.setResultValue((String) req.get("resultValue"));
        r.setNormalRange((String) req.getOrDefault("normalRange", test.getNormalRange()));
        r.setUnit((String) req.getOrDefault("unit", test.getUnit()));
        r.setAbnormal(Boolean.TRUE.equals(req.get("isAbnormal")));
        r.setRemarks((String) req.get("remarks"));
        r.setVerifiedBy(verifiedBy != null ? verifiedBy : "Senior Biochemist");
        r = labResultRepository.save(r);

        lo.setStatus("COMPLETED");
        labOrderRepository.save(lo);

        Charge charge = new Charge();
        charge.setPatient(lo.getPatient());
        charge.setSourceModule("LAB");
        charge.setSourceId(lo.getId());
        charge.setServiceName("Diagnostic Investigation - " + test.getName());
        charge.setQuantity(1);
        charge.setUnitPrice(test.getPrice() != null ? test.getPrice() : 500.0);
        charge.setTotalAmount(charge.getUnitPrice());
        charge.setStatus("UNBILLED");
        chargeRepository.save(charge);

        return r;
    }
}
