package com.SmartEvent.SmartEvent.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "commentaires")
public class Commentaire {

    @Id
    private String id;

    @NotBlank
    private String eventId;

    @NotBlank
    private String userId;

    @NotBlank
    private String content;

    private LocalDateTime date = LocalDateTime.now();

    // ----- Constructors -----
    public Commentaire() {}

    public Commentaire(String id, String eventId, String userId, String content, LocalDateTime date) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.content = content;
        this.date = date;
    }

    // ----- Getters & Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
