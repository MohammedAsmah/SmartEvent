package com.SmartEvent.SmartEvent.Filters;

import com.SmartEvent.SmartEvent.Security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class  JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/login"); // endpoint for login
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Read JSON body → {"username":"simo","password":"1234"}
            Map<String, String> creds = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            String username = creds.get("userName");
            String password = creds.get("password");

            // Create an authentication object → Spring will check with UserDetailsService
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        // If login succeeds, generate token
        String username = authResult.getName();
        String role = authResult.getAuthorities().iterator().next().getAuthority();

        String token = jwtUtil.generateToken(username);

        // Send token as JSON response
        new ObjectMapper().writeValue(response.getOutputStream(), Map.of("token", token));
    }
}
