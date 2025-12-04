package com.SmartEvent.SmartEvent.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "invites")
public class Invite  {

    @Id
    private String id;

    @NotBlank
    private String eventId;

    @Email
    @NotBlank
    private String email;

    private boolean confirmed = false;

    // ----- Constructors -----
    public Invite() {}

    public Invite(String id, String eventId, String email, boolean confirmed) {
        this.id = id;
        this.eventId = eventId;
        this.email = email;
        this.confirmed = confirmed;
    }

    // ----- Getters & Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}
