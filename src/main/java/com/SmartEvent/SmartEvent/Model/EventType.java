package com.SmartEvent.SmartEvent.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection = "event_types")
public class EventType {

    @Id
    private String id;

    @NotBlank(message = "Le nom du type est obligatoire")
    @Size(max = 100, message = "Le nom du type doit contenir au maximum 100 caractères")
    private String name;

    @Size(max = 300, message = "La description doit contenir au maximum 300 caractères")
    private String description;

    public EventType() {}

    public EventType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // --- Getters & Setters ---
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
