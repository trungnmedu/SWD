package com.swd.backend.utils;

import io.jsonwebtoken.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class JwtTokenUtils {
    private final String secretKey = "SWP391";

    @Bean
    public JwtTokenUtils getJwtTokenUtil() {
        return new JwtTokenUtils();
    }

    public Claims deCodeToken(String token) throws SignatureException, MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String doGenerateToken(String userId, String fullName, String email, String phone, String role, boolean isConfirm) {
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expirationAt = DateHelper.plusMinutes(createAt, 28800);

        return Jwts.builder()
                .setSubject(userId)
                .claim("fullName", fullName)
                .claim("email", email)
                .claim("phone", phone)
                .claim("role", role)
                .claim("isConfirmed", isConfirm)
                .setIssuedAt(createAt)
                .setExpiration(expirationAt)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
