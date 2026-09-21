package com.aegiscare.hospital.security;

import com.aegiscare.hospital.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:hospital_management_secret_key_2026_super_secure_enterprise_key_aegiscare_demo}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:604800000}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(CustomUserDetails userDetails) {
        return generateToken(userDetails.getId(), userDetails.getUsername(), userDetails.getRole().name(),
                userDetails.getFirstName(), userDetails.getLastName(),
                userDetails.getPatientId(), userDetails.getDoctorId(), userDetails.getEmployeeId());
    }

    public String generateToken(User user, String patientId, String doctorId, String employeeId) {
        return generateToken(user.getId(), user.getEmail(), user.getRole().name(),
                user.getFirstName(), user.getLastName(),
                patientId, doctorId, employeeId);
    }

    private String generateToken(String userId, String email, String role, String firstName, String lastName,
                                String patientId, String doctorId, String employeeId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("firstName", firstName);
        claims.put("lastName", lastName);
        if (patientId != null) claims.put("patientId", patientId);
        if (doctorId != null) claims.put("doctorId", doctorId);
        if (employeeId != null) claims.put("employeeId", employeeId);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}