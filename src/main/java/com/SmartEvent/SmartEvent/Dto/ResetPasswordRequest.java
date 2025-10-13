package com.SmartEvent.SmartEvent.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {
    @NotBlank(message = "email is requered")
    @Email(message = "please orivide a valid email")
    private String email;
    @Max(4)
    private int pin;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getPin() {
        return pin;
    }
    public void setPin(int pin) {
        this.pin = pin;
    }
}
