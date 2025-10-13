package com.SmartEvent.SmartEvent.Model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Document
public class User implements UserDetails {
    @Id
    private String _id;
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
    @NotNull(message = "phone number is requered")
    private int PhoneNumber;
    private String photo;
    private boolean Status=true;
    @NotNull(message = "role is requered")
    private String role;
    private String resetCode;
    private LocalDateTime resetCodeExpiry;

    public User(String firstName, String lastName, String email, String password, String userName, int PhoneNumber,String photo, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = userName;
        this.PhoneNumber = PhoneNumber;
        this.photo =photo;
        this.role = role;
        this.Status = true;
    }

    public User() {

    }

    public String getId() {
        return _id;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public int getPhoneNumber() {
        return PhoneNumber;
    }
    public String getResetCode() {
        return resetCode;
    }
    public LocalDateTime getResetCodeExpiry() {
        return resetCodeExpiry;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserName(String userName) {
        this.username = userName;
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
    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
    public void setResetCodeExpiry(LocalDateTime resetCodeExpiry) {
        this.resetCodeExpiry = resetCodeExpiry;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role ;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhoto() {
        return photo;
    }

    public void setId(String id) {
        this._id = id;
    }
    public void setStatus(boolean status) {
        this.Status = status;
    }
    public boolean isStatus() {
        return Status;
    }
}
