// package com.SmartEvent.SmartEvent.Controller;
package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Enums.EventStatus;
import com.SmartEvent.SmartEvent.Model.Event;
import com.SmartEvent.SmartEvent.Model.EventType;
import com.SmartEvent.SmartEvent.Service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/events")
@Validated
public class EventController {

    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(consumes = {"multipart/form-data","application/json"})
    public ResponseEntity<Event> createEvent(
            @RequestPart("event") @Valid Event event,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "couverture", required = false) MultipartFile couverture
    ) {
        List<MultipartFile> imgs = (images == null) ? null : Arrays.asList(images);
        Event saved = eventService.createEventWithImages(event, imgs,logo, couverture);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        return eventService.getEventById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found"));
    }


    /**
     * ✅ Update event
     * Example: PUT /api/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody Event event) {
        try {
            Event updated = eventService.updateEvent(id, event);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * ✅ Delete event
     * Example: DELETE /api/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok("Event deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * ✅ Get paginated list of events
     * Example: GET /api/events?page=0&size=5
     */
    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Event> events = eventService.getEvents(page, size);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/filter")
    public ResponseEntity<Page<Event>> filterEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end
    ) {
        Page<Event> events = eventService.filterEvents(page,size,status, type, start, end);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/create")
    public ResponseEntity<Map<String,Object>> get_create(){
        Map<String,Object> map = new HashMap<>();
        map.put("eventStatus",EventStatus.values());
        map.put("eventsType",EventType.values());
        return ResponseEntity.ok(map);
    }


    @PostMapping("/archive-past")
    public ResponseEntity<String> archivePastEvents() {
        eventService.archivePastEvents();
        return ResponseEntity.ok("Past events archived successfully!");
    }

    @GetMapping("/archived")
    public ResponseEntity<List<Event>> getArchivedEvents() {
        return ResponseEntity.ok(eventService.getArchivedEvents());
    }
}
