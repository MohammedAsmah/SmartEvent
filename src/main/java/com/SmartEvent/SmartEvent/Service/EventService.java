// package com.SmartEvent.SmartEvent.Service;
package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Enums.EventStatus;
import com.SmartEvent.SmartEvent.Model.Event;
import com.SmartEvent.SmartEvent.Model.Image;
import com.SmartEvent.SmartEvent.Repository.EventRepository;
import com.SmartEvent.SmartEvent.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;

    // configurable base upload dir (you can put this in application.properties)
    @Value("${app.upload.dir:uploads/events}")
    private String baseUploadDir;

    public EventService(EventRepository eventRepository, ImageRepository imageRepository) {
        this.eventRepository = eventRepository;
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    private void ensureBaseDir() {
        try {
            Files.createDirectories(Paths.get(baseUploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create upload base directory: " + baseUploadDir, e);
        }
    }

    /**
     * Create an event and save uploaded images.
     * Steps:
     *   1) Save event (without images) so we get an id.
     *   2) Save each image file to disk under uploads/events/{eventId}/.
     *   3) Create Image docs, save them, collect their ids.
     *   4) Update the event with imageIds and save it.
     */
    public Event createEventWithImages(Event event, List<MultipartFile> images) {
        // Basic validation example: ensure date order (if your Event uses LocalDate/LocalDateTime)
        if (event.getStartDate() != null && event.getEndDate() != null) {
            if (event.getEndDate().isBefore(event.getStartDate())) {
                throw new IllegalArgumentException("endDate must be after startDate");
            }
        }
        LocalDate today = LocalDate.now();

        if (event.getStartDate() != null && event.getEndDate() != null) {
            if (today.isBefore(event.getStartDate().toLocalDate())) {
                event.setStatus(EventStatus.UPCOMING);
            } else if (today.isAfter(event.getEndDate().toLocalDate())) {
                event.setStatus(EventStatus.COMPLETED);
            } else {
                // today is between startDate and endDate
                event.setStatus(EventStatus.ONGOING);
            }
        } else {
            // If dates are not provided, set default status
            event.setStatus(EventStatus.DRAFT);
        }

        // 1) Save event first to generate id
        event.setImageIds(new ArrayList<>()); // ensure not null
        Event saved = eventRepository.save(event);

        // 2) prepare event upload dir
        Path eventDir = Paths.get(baseUploadDir, saved.getId());
        try {
            Files.createDirectories(eventDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create event directory: " + eventDir.toString(), e);
        }

        // 3) process images
        if (images != null) {
            List<String> imageIds = new ArrayList<>();
            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) continue;

                // sanitize filename (very basic)
                String original = Paths.get(file.getOriginalFilename()).getFileName().toString();
                String filename = saved.getId() + "_" + Instant.now().toEpochMilli() + "_" + original;
                Path target = eventDir.resolve(filename);

                try {
                    // copy file to disk
                    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                    // create image document (url stores relative path; adapt as needed)
                    Image image = new Image();
                    image.setUrl("/" + baseUploadDir + "/" + saved.getId() + "/" + filename); // or store absolute path
                    image.setEventId(saved.getId());
                    image.setDescription(null);

                    Image savedImage = imageRepository.save(image);
                    imageIds.add(savedImage.getId());
                } catch (IOException e) {
                    // if one image fails, we continue but log/throw depending on your needs
                    throw new RuntimeException("Failed to store file " + original, e);
                }
            }

            // 4) update event with image ids and save
            saved.setImageIds(imageIds);
            saved = eventRepository.save(saved);
        }

        return saved;
    }

    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(String id, Event update) {
        Event exist = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        // update fields you allow
        exist.setTitre(update.getTitre());
        exist.setType(update.getType());
        exist.setStatus(update.getStatus());
        exist.setDescription(update.getDescription());
        exist.setStartDate(update.getStartDate());
        exist.setEndDate(update.getEndDate());
        exist.setLocalisation(update.getLocalisation());
        exist.setCouverture(update.getCouverture());
        exist.setLogo(update.getLogo());
        // do not override imageIds here unless intended
        return eventRepository.save(exist);
    }

    public void deleteEvent(String id) {
        // delete images files & docs
        List<Image> images = imageRepository.findByEventId(id);
        for (Image img : images) {
            // delete file from disk (if url was stored as relative path)
            try {
                String url = img.getUrl(); // example: /uploads/events/{eventId}/{filename}
                if (url != null) {
                    Path p = Paths.get(url.replaceFirst("^/", ""));
                    if (Files.exists(p)) {
                        Files.delete(p);
                    }
                }
            } catch (Exception ignored) { }
        }
        imageRepository.deleteByEventId(id);

        // delete event directory (optional)
        try {
            Path eventDir = Paths.get(baseUploadDir, id);
            if (Files.exists(eventDir)) {
                // delete files then directory
                Files.walk(eventDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException ignored) {}

        // delete event doc
        eventRepository.deleteById(id);
    }
    public Page<Event> getEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findAll(pageable);
    }
    public List<Event> filterEvents(EventStatus status, String localisation, LocalDateTime start, LocalDateTime end) {
        // Start with all events
        List<Event> allEvents = eventRepository.findAll();

        return allEvents.stream()
                .filter(e -> status == null || e.getStatus() == status)
                .filter(e -> localisation == null || e.getLocalisation().toLowerCase().contains(localisation.toLowerCase()))
                .filter(e -> {
                    if (start != null && end != null) {
                        return !e.getStartDate().isBefore(start) && !e.getEndDate().isAfter(end);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    public void archivePastEvents() {
        LocalDate today = LocalDate.now();
        List<Event> pastEvents = eventRepository.findByendDateBefore(today);

        for (Event e : pastEvents) {
            if (!"ARCHIVED".equals(e.getStatus())) {
                e.setStatus(EventStatus.ARCHIVED);
                eventRepository.save(e);
            }
        }
    }

    public List<Event> getArchivedEvents() {
        return eventRepository.findByStatus(EventStatus.ARCHIVED);
    }
}
