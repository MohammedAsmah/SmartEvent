package com.SmartEvent.SmartEvent.Dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.index.Indexed;


public class RegisterRequestDto {
    @NotBlank(message = "firstname is requered")
    private String firstName;
    @NotBlank(message = "lastname is requered")
    private String lastName;
    @NotBlank(message = "email is requered")
    @Email(message = "please orivide a valid email")
    @Indexed(unique = true)
    private String email;
    @NotBlank(message = "password is requered")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;
    @NotBlank(message = "username is requered")
    @Indexed(unique = true)
    private String username;
    @NotNull
    @Indexed(unique = true)
    private int PhoneNumber;
    public RegisterRequestDto(String firstName, String lastName, String email, String password, String userName, int PhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = userName;
        this.PhoneNumber = PhoneNumber;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public int getPhoneNumber() {
        return PhoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPhoneNumber(int PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
