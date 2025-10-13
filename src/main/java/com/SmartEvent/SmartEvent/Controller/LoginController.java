package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Dto.LoginRequestDto;
import com.SmartEvent.SmartEvent.Dto.TokenResponseDto;
import com.SmartEvent.SmartEvent.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth/")
public class LoginController {
    private AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login/")
    public TokenResponseDto login(@RequestBody LoginRequestDto request) {
        return authService.Login(request.getUsername(), request.getPassword());
    }

    @PostMapping("refresh-token/")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestHeader("Refresh-Token") String refreshToken,@RequestHeader("token") String token) {
        TokenResponseDto newTokens = authService.refreshToken(refreshToken,token);
        return ResponseEntity.ok(newTokens);
    }
    @PostMapping("logout/")
    public String logout(@RequestHeader("Refresh-Token") String refreshToken,@RequestHeader("token") String token) {
        return authService.LogOut(token, refreshToken);
    }
}
