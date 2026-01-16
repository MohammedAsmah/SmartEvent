package com.SmartEvent.SmartEvent.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import jakarta.validation.constraints.NotNull;

@Document(collection = "invites")
@Data
public class Invite {

    @Id
    private String id;

    @NotNull
    private String eventId;

    @NotNull
    private String personId;

    @NotNull
    private Invitation invitation;

    private boolean confirmed = false;

    public Invite() {}

    public Invite(String eventId, String personId, boolean confirmed) {
        this.eventId = eventId;
        this.personId = personId;
        this.confirmed = confirmed;
    }
}
