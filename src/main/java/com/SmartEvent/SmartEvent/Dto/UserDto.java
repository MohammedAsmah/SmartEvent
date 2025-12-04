package com.SmartEvent.SmartEvent.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.index.Indexed;

public class UserDto {
    private String id;
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
    private String userName;
    @NotNull
    private int PhoneNumber;
    private String role;
    private String photo;
    public UserDto(String firstName, String lastName, String email, String password, String userName, int PhoneNumber, String role, String photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.PhoneNumber = PhoneNumber;
        this.role = role;
        this.photo = photo;
    }
    public UserDto() {}
    public String getId(){
        return userName;
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
    public String getUserName() {
        return userName;
    }
    public int getPhoneNumber() {
        return PhoneNumber;
    }
    public String getRole() {
        return role;
    }
    public String getPhoto() {
        return photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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
    public void setRole(String role) {
        this.role = role;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setId(String id) {
        this.id=id;
    }
}

