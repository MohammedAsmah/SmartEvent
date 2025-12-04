package com.SmartEvent.SmartEvent.Controller;


import com.SmartEvent.SmartEvent.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ResetPasswordController {
    private final AuthService authService;

    public ResetPasswordController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Map> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return authService.forgotPassword(email);
    }
    @PostMapping("/check_code")
        public ResponseEntity<Map> checlCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        return authService.checkCode(code);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Map> resetPassword(@RequestBody Map<String, String> request) {
        return authService.resetPassword(request.get("code"), request.get("newPassword"));
    }
}
