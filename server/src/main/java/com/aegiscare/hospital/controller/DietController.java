package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.entity.DietPlan;
import com.aegiscare.hospital.entity.MealFulfillment;
import com.aegiscare.hospital.service.DietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diet")
public class DietController {

    @Autowired
    private DietService dietService;

    @GetMapping("/admitted-patients")
    public ResponseEntity<Map<String, Object>> getAdmittedPatientsForDiet() {
        List<Map<String, Object>> list = dietService.getAdmittedPatientsForDiet();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("admissions", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> getAllDietPlans() {
        List<DietPlan> list = dietService.getAllDietPlans();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("dietPlans", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/plan")
    public ResponseEntity<Map<String, Object>> createDietPlan(@RequestBody Map<String, Object> body, Authentication auth) {
        DietPlan plan = dietService.createDietPlan(body, auth != null ? auth.getName() : null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("dietPlan", plan);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/fulfillments")
    public ResponseEntity<Map<String, Object>> getMealFulfillments() {
        List<MealFulfillment> list = dietService.getMealQueue(LocalDate.now());
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("fulfillments", list);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/fulfillments/{id}/status")
    public ResponseEntity<Map<String, Object>> updateMealStatus(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String status = (String) body.get("status");
        MealFulfillment mf = dietService.updateMealStatus(id, status);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("fulfillment", mf);
        return ResponseEntity.ok(res);
    }
}