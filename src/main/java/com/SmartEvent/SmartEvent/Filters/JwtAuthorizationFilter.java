package com.SmartEvent.SmartEvent.Filters;

import com.SmartEvent.SmartEvent.Exception.ForbiddenException;
import com.SmartEvent.SmartEvent.Model.Token;
import com.SmartEvent.SmartEvent.Repository.TokenRepository;
import com.SmartEvent.SmartEvent.Security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private TokenRepository  tokenRepository;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Skip JWT validation for auth endpoints (login, register, etc.)
        String requestPath = request.getRequestURI();
        if (requestPath != null && requestPath.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            System.out.println("🟢 Extracted username from token: " + username);
            if (username == null) {
                System.out.println("❌ Invalid or malformed token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or malformed token\"}");
                response.setContentType("application/json");
                return; }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Check if token exists in database
                Token savedToken = tokenRepository.findByUsername(username)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid or expired token"
                        ));

                // Verify token matches
                if (!savedToken.getAccessToken().equals(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Invalid token\"}");
                    response.setContentType("application/json");
                    return;
                }

                // Validate token
                if (!jwtUtil.validateToken(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Token expired\"}");
                    response.setContentType("application/json");
                    return;
                }

                // Set authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("✅ Authentication set for user: " + username);
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println("❌ JWT Authentication error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Authentication failed\"}");
            response.setContentType("application/json");
        }
    }
}