package com.SmartEvent.SmartEvent.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter

public abstract class Person {
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
    public Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    Person() {}
}
