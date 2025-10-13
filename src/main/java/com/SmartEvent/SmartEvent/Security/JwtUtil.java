package com.SmartEvent.SmartEvent.Security;

import com.SmartEvent.SmartEvent.Model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;

@Component
public class JwtUtil {
    private final String SECRET = "mysupersecuresecretkeyofatleast32chars123!"; // use at least 32 chars
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1h

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            System.out.println("🟢 Extracted username: " + username);
            return username;
        } catch (Exception e) {
            System.out.println("🔴 Failed to extract username: " + e.getMessage());
            return null;
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractRole(String token) {

        return token;
    }
    public String GetEXPIRATION_TIME(){
        return String.valueOf(EXPIRATION_TIME+"second");
    }
    public String getSECRET() {
        return SECRET;
    }

}
