package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Dto.RegisterRequestDto;
import com.SmartEvent.SmartEvent.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/register/")
public class RegisterController {
    private AuthService authService;
    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("")
    public ResponseEntity<Map> register(@RequestBody @Valid RegisterRequestDto request){
        return authService.Register(request.getFirstName(),request.getLastName(),request.getEmail(),request.getPassword(),request.getUsername(),request.getPhoneNumber());
    }
}
