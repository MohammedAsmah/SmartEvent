package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Dto.RegisterRequestDto;
import com.SmartEvent.SmartEvent.Service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/register/")
public class RegisterController {
    private AuthService authService;
    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("")
    public String register(@RequestBody RegisterRequestDto request){
        return authService.Register(request.getFirstName(),request.getLastName(),request.getEmail(),request.getPassword(),request.getUsername(),request.getPhoneNumber());
    }
}
