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
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.resetPassword(request.get("code"), request.get("newPassword")));
    }
}
