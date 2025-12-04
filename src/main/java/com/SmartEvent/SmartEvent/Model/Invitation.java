package com.SmartEvent.SmartEvent.Model;

import com.SmartEvent.SmartEvent.Enums.InvitationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invitations")
public class Invitation {

    @Id
    private String id;

    @DBRef
    private Event event; // Linked event

    @DBRef
    private User invite; // Invited user (or String email if you prefer)

    private InvitationStatus status; // Now uses the enum

    private String qrCode; // Can store URL or Base64 QR code

    public Invitation() {
        this.status = InvitationStatus.PENDING;
    }

    public Invitation(Event event, User invite, String qrCode) {
        this.event = event;
        this.invite = invite;
        this.qrCode = qrCode;
        this.status = InvitationStatus.PENDING;
    }

    // -------------------- Getters & Setters --------------------
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getInvite() {
        return invite;
    }

    public void setInvite(User invite) {
        this.invite = invite;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
