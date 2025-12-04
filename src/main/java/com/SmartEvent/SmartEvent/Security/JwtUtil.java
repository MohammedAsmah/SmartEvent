package com.SmartEvent.SmartEvent.Security;

import com.SmartEvent.SmartEvent.Model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration:3600000}") // 1 hour default
    private long EXPIRATION_TIME;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private long REFRESH_EXPIRATION_TIME;

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
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
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
        } catch (ExpiredJwtException e) {
            System.out.println("🔴 Token expired: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("🔴 Failed to extract username: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("🔴 Token expired");
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("🔴 Malformed token");
            return false;
        } catch (SecurityException | SignatureException e) {
            System.out.println("🔴 Invalid signature");
            return false;
        } catch (Exception e) {
            System.out.println("🔴 Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    // Extract claims for additional info
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public long getRefreshExpirationTime() {
        return REFRESH_EXPIRATION_TIME;
    }
    public String getSECRET(){
        return SECRET;
    }

    // Remove or fix this method - it currently just returns the token
    // If you want to extract roles, add them to the token first:
    /*
    public String generateTokenWithRole(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }
    */
}