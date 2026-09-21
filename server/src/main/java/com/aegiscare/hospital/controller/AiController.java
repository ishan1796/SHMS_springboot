package com.aegiscare.hospital.controller;

import com.aegiscare.hospital.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AIService aiService;

    @GetMapping("/executive-summary")
    public ResponseEntity<Map<String, Object>> getExecutiveSummary(
            @RequestParam(defaultValue = "ADMIN") String role) {
        return ResponseEntity.ok(aiService.getExecutiveSummary(role));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        String prompt = (String) body.get("prompt");
        String role = (String) body.getOrDefault("role", "ADMIN");
        String userName = auth != null ? auth.getName() : "Staff Member";
        return ResponseEntity.ok(aiService.handleChat(prompt, role, userName));
    }
}