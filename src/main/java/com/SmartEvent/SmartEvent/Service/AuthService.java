package com.SmartEvent.SmartEvent.Service;
import com.SmartEvent.SmartEvent.Filters.SecurityConfig.*;

import com.SmartEvent.SmartEvent.Dto.TokenResponseDto;
import com.SmartEvent.SmartEvent.Model.Token;
import com.SmartEvent.SmartEvent.Repository.TokenRepository;
import com.SmartEvent.SmartEvent.Service.EmailService;
import com.SmartEvent.SmartEvent.Exception.GlobalExceptionHandler;
import com.SmartEvent.SmartEvent.Model.User;
import com.SmartEvent.SmartEvent.Repository.UserRepository;
import com.SmartEvent.SmartEvent.Security.JwtUtil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    private static UserRepository userRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private TokenRepository tokenRepository;
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, EmailService emailService, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }
    public String Register(String firstName, String lastName, String email, String password, String userName, int PhoneNumber){
        if(userRepository.findByUsername(userName) != null){
            throw new IllegalArgumentException("Username already taken!");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user=new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setUserName(userName);
        user.setPhoneNumber(PhoneNumber);
        user.setRole("USER");
        userRepository.save(user);
        return "user saved successfully!";
    }
    public TokenResponseDto Login(String username, String password){
        User user=userRepository.findByUsername(username);
        if(user==null || !passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("Username or password incorrect!");
        }else{
                String jwt=jwtUtil.generateToken(username);
                String refreshToken=jwtUtil.generateRefreshToken(user);
                TokenResponseDto tokenResponseDto=new TokenResponseDto();
                tokenResponseDto.setToken(jwt);
                tokenResponseDto.setRefreshToken(refreshToken);
                Token usToken=tokenRepository.findByUsername(username);
                if(usToken==null){
            Token token=new Token();
            token.setAccessToken(jwt);
            token.setRefreshToken(refreshToken);
            token.setUsername(username);
            token.setExpirationTime(jwtUtil.GetEXPIRATION_TIME());
            tokenRepository.save(token);}else {
                    usToken.setAccessToken(jwt);
                    usToken.setRefreshToken(refreshToken);
                    usToken.setUsername(username);
                    usToken.setExpirationTime(jwtUtil.GetEXPIRATION_TIME());
                    tokenRepository.save(usToken);
                }
            System.out.println(username);
            return tokenResponseDto;
        }
    }
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email not found!");
        }

        // Generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(9999));

        user.setResetCode(code);
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(10)); // valid for 10 min
        userRepository.save(user);

        Context context = new Context();
        context.setVariable("name", user.getFirstName()); // replaces ${name} in template
        context.setVariable("code", code);
        // Send email
        emailService.sendEmailWithTemplate(email, "Password Reset Code", "password-reset",context);

        return "Reset code sent to your email.";
    }
    public String resetPassword(String code, String newPassword) {
        User user = userRepository.findByResetCode(code);
        if (user == null || user.getResetCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired code!");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetCode(null);
        user.setResetCodeExpiry(null);
        userRepository.save(user);

        return "Password updated successfully!";
    }
    public TokenResponseDto refreshToken(String refreshToken,String token) {
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token));
        Token storedToken=tokenRepository.findByUsername(username);
        if(storedToken != null){
        if(!storedToken.getRefreshToken().equals(refreshToken)){
            throw new IllegalArgumentException("Invalid refresh token!");

        }}
        if(user == null){
            throw new IllegalArgumentException("Username not found!");
        }

        // Validate the refresh token
        try {
            Jwts.parserBuilder().setSigningKey(jwtUtil.getSECRET().getBytes()).build().parseClaimsJws(refreshToken);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        TokenResponseDto tokenResponseDto=new TokenResponseDto();
        tokenResponseDto.setToken(newAccessToken);
        tokenResponseDto.setRefreshToken(refreshToken);
        Token tokenUser=tokenRepository.findByUsername(username);
        tokenUser.setAccessToken(newAccessToken);
        tokenRepository.save(tokenUser);
        return tokenResponseDto;
    }
    public String LogOut(String token, String refreshToken) {
        tokenRepository.deleteByUsername(jwtUtil.extractUsername(token));
        return "Logged out successfully!";
    }

}
