// package com.SmartEvent.SmartEvent.Service;
package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Enums.EventStatus;
import com.SmartEvent.SmartEvent.Model.Event;
import com.SmartEvent.SmartEvent.Model.EventType;
import com.SmartEvent.SmartEvent.Model.Image;
import com.SmartEvent.SmartEvent.Repository.EventRepository;
import com.SmartEvent.SmartEvent.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


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
    public Event createEventWithImages(Event event,
                                       List<MultipartFile> images,
                                       MultipartFile logoFile,
                                       MultipartFile couvertureFile) {

        // --- 1) Validate dates ---
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
                event.setStatus(EventStatus.ONGOING);
            }
        } else {
            event.setStatus(EventStatus.DRAFT);
        }

        // --- 2) Save event first to generate ID ---
        event.setImageIds(new ArrayList<>());
        event.setType(EventType.CONFERENCE);
        Event saved = eventRepository.save(event);

        // --- 3) Prepare event upload directory ---
        Path eventDir = Paths.get(baseUploadDir, saved.getId());
        try {
            Files.createDirectories(eventDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create event directory: " + eventDir.toString(), e);
        }

        // --- 4) Save logo ---
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoFilename = saved.getId() + "_logo_" + Instant.now().toEpochMilli() + "_" +
                    Paths.get(logoFile.getOriginalFilename()).getFileName().toString();
            Path target = eventDir.resolve(logoFilename);
            try {
                Files.copy(logoFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                saved.setLogo("/" + baseUploadDir + "/" + saved.getId() + "/" + logoFilename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store logo file", e);
            }
        }

        // --- 5) Save couverture (cover) ---
        if (couvertureFile != null && !couvertureFile.isEmpty()) {
            String coverFilename = saved.getId() + "_cover_" + Instant.now().toEpochMilli() + "_" +
                    Paths.get(couvertureFile.getOriginalFilename()).getFileName().toString();
            Path target = eventDir.resolve(coverFilename);
            try {
                Files.copy(couvertureFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                saved.setCouverture("/" + baseUploadDir + "/" + saved.getId() + "/" + coverFilename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store couverture file", e);
            }
        }

        // --- 6) Save other images ---
        if (images != null) {
            List<String> imageIds = new ArrayList<>();
            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) continue;

                String original = Paths.get(file.getOriginalFilename()).getFileName().toString();
                String filename = saved.getId() + "_" + Instant.now().toEpochMilli() + "_" + original;
                Path target = eventDir.resolve(filename);

                try {
                    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                    Image image = new Image();
                    image.setUrl("/" + baseUploadDir + "/" + saved.getId() + "/" + filename);
                    image.setEventId(saved.getId());
                    image.setDescription(null);

                    Image savedImage = imageRepository.save(image);
                    imageIds.add(savedImage.getId());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store file " + original, e);
                }
            }
            saved.setImageIds(imageIds);
        }

        // --- 7) Final save ---
        saved = eventRepository.save(saved);

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
    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Event> filterEvents(int page,int size,
                                    EventStatus status,
                                    EventType type,
                                    LocalDate start,
                                    LocalDate end) {

        Pageable pageable = PageRequest.of(page, size);
        Query query = new Query().with(pageable);

        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }

        if (start != null) {
            query.addCriteria(
                    Criteria.where("startDate").gte(start.atStartOfDay())

            );
        }
        if (end != null) {
            query.addCriteria(
                    Criteria.where("endDate").lte(end.atTime(23,59,59))
            );
        }

        List<Event> events = mongoTemplate.find(query, Event.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Event.class);

        return new PageImpl<>(events, pageable, total);
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
