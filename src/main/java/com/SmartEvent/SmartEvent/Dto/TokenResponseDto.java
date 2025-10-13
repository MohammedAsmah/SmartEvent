package com.SmartEvent.SmartEvent.Dto;

import jakarta.validation.constraints.NotBlank;


public class TokenResponseDto {
    @NotBlank(message = "token required")
    private String token;
    @NotBlank(message = "time of token required")
    private String refreshToken;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String tokenTime) {
        this.refreshToken = tokenTime;
    }
}
