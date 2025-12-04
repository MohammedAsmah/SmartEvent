package com.SmartEvent.SmartEvent.Model;

import com.SmartEvent.SmartEvent.Enums.EventStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "events")
public class Event {

    @Id
    private String id;

    // Cover image path or URL
    @Size(max = 500, message = "Couverture path must be at most 500 characters")
    private String couverture;

    // Logo image path or URL
    @Size(max = 500, message = "Logo path must be at most 500 characters")
    private String logo;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 150, message = "Le titre doit contenir au maximum 150 caractères")
    private String titre;

    // Reference to EventType document
    //@DBRef
    @NotNull(message = "Le type d'événement est obligatoire")
    private EventType type;

    //@NotNull(message = "Le statut est obligatoire")
    private EventStatus status;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 5000, message = "La description est trop longue")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDate;

    @NotBlank(message = "La localisation est obligatoire")
    @Size(max = 300, message = "La localisation doit contenir au maximum 300 caractères")
    private String localisation;
    private List<String> imageIds = new ArrayList<>();
    private List<String> inviteIds = new ArrayList<>();
    private List<String> commentaireIds = new ArrayList<>();

    public Event() {}

    // --- Getters & Setters ---

    public String getId() {
        return id;
    }

    public String getCouverture() {
        return couverture;
    }

    public void setCouverture(String couverture) {
        this.couverture = couverture;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public List<String> getImageIds() { return imageIds; }
    public void setImageIds(List<String> imageIds) { this.imageIds = imageIds; }

    public List<String> getInviteIds() { return inviteIds; }
    public void setInviteIds(List<String> inviteIds) { this.inviteIds = inviteIds; }

    public List<String> getCommentaireIds() { return commentaireIds; }
    public void setCommentaireIds(List<String> commentaireIds) { this.commentaireIds = commentaireIds; }

    // --- Validation helper: ensure endDate is after startDate ---
    @AssertTrue(message = "La date de fin doit être après la date de début")
    private boolean isValidDateRange() {
        if (startDate == null || endDate == null) return true;
        return endDate.isAfter(startDate);
    }

    // Default status if not set
    public void prePersist() {
        if (status == null) {
            status = EventStatus.DRAFT;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
