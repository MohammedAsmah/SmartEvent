package com.SmartEvent.SmartEvent.Model;

import com.SmartEvent.SmartEvent.Enums.InvitationStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invitations")
@Getter
@Setter
public class Invitation {

    @Id
    private String id;

    @DBRef
    private Event event; // Linked event

    @DBRef
    private Person invite; // Invited user (or String email if you prefer)

    private InvitationStatus status; // Now uses the enum

    private String qrCode; // Can store URL or Base64 QR code

    private String validationToken ;

    public Invitation() {
        this.status = InvitationStatus.PENDING;
    }

    public Invitation(Event event, Person invite, String qrCode, String validationToken) {
        this.event = event;
        this.invite = invite;
        this.qrCode = qrCode;
        this.status = InvitationStatus.PENDING;
        this.validationToken = validationToken;
    }
    public Invitation(Event event, Person invite, String validationToken) {
        this.event = event;
        this.invite = invite;
        this.status = InvitationStatus.PENDING;
        this.validationToken = validationToken;
    }


}
