package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Enums.EventStatus;
import com.SmartEvent.SmartEvent.Model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    Event findByTitle(String title);
    List<Event> findByStatus(EventStatus status);

    // Filter by localisation (contains)
    List<Event> findByLocalisationContainingIgnoreCase(String localisation);

    // Filter by date range (events within a specific period)
    @Query("{ 'startDate': { $gte: ?0 }, 'endDate': { $lte: ?1 } }")
    List<Event> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Event> findByendDateBefore(LocalDate date);
    List<Event> findByStatus(String status);
}

