package com.SmartEvent.SmartEvent.Service;
import com.SmartEvent.SmartEvent.Exception.ForbiddenException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.util.Map;
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
    public ResponseEntity<Map> Register(String firstName, String lastName, String email, String password, String userName, int PhoneNumber){
        if(userRepository.findByUsername(userName) != null){
            throw new IllegalArgumentException("Username already taken!");
        }
        if(userRepository.findByEmail(email) != null){
            throw new IllegalArgumentException("Email already taken!");
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
        return ResponseEntity.ok(Map.of("message", "User saved successfully"));  }
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
                Token usToken=tokenRepository.findByUsername(username).orElse(new Token());
                if(usToken.getUsername()== null){
            Token token=new Token();
            token.setAccessToken(jwt);
            token.setRefreshToken(refreshToken);
            token.setUsername(username);
            token.setExpirationTime(jwtUtil.getExpirationTime());
            tokenRepository.save(token);}else {
                    usToken.setAccessToken(jwt);
                    usToken.setRefreshToken(refreshToken);
                    usToken.setUsername(username);
                    usToken.setExpirationTime(jwtUtil.getExpirationTime());
                    tokenRepository.save(usToken);
                }
            System.out.println(username);
            return tokenResponseDto;
        }
    }
    public ResponseEntity<Map> forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("email not found!"));

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

        return ResponseEntity.ok(Map.of("message", "a code with 4 digits sents to your email!"));
    }

//    checking the code if corect
    public ResponseEntity<Map> checkCode(String code) {
        User user = userRepository.findByResetCode(code).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid or expired CODE"
        ));
        if (!(user == null)  && !(user.getResetCodeExpiry().isBefore(LocalDateTime.now())) ) {
            return ResponseEntity.ok(Map.of("message", "YOU CODE IS VALID!"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired code!"));
    }

    public ResponseEntity<Map> resetPassword(String code, String newPassword) {
        User user = userRepository.findByResetCode(code).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid or expired CODE"
        ));
        if (user == null && user.getResetCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired code!");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetCode(null);
        user.setResetCodeExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok().body(Map.of("message", "code updates successfully"));
    }
    public TokenResponseDto refreshToken(String refreshToken,String token) {
        String tk = token.substring(7);
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(jwtUtil.extractUsername(refreshToken));
        Token storedToken=tokenRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or expired token"
        ));
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
        Token tokenUser=tokenRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or expired token"
        ));
        tokenUser.setAccessToken(newAccessToken);
        tokenRepository.save(tokenUser);
        return tokenResponseDto;
    }
    public ResponseEntity<String> ckeckToken(String token) {
        String tk = token.substring(7);
        Token ustoken=tokenRepository.findByUsername(jwtUtil.extractUsername(tk)).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or expired token"
        ));
        if(ustoken.getAccessToken().equals(tk)){
            if(jwtUtil.validateToken(tk)){
                return ResponseEntity.ok("token is valid");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    public ResponseEntity<String> LogOut(String token, String refreshToken) {
        String tk = token.substring(7);
        if(tokenRepository.deleteByUsername(jwtUtil.extractUsername(tk))>0){
            return ResponseEntity.ok("Log out successfully.");
        }
        return ResponseEntity.ok("Log out failed.");
    }
}
