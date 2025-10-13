package com.SmartEvent.SmartEvent.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "images")
public class Image {

    @Id
    private String id;

    @NotBlank
    private String url;

    @NotBlank
    private String eventId;

    private String description;

    // ----- Constructors -----
    public Image() {}

    public Image(String id, String url, String eventId, String description) {
        this.id = id;
        this.url = url;
        this.eventId = eventId;
        this.description = description;
    }

    public Image(String url, String eventId) {
        this.url = url;
        this.eventId = eventId;
    }

    // ----- Getters & Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
