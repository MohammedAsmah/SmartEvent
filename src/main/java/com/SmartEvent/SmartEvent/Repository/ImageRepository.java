package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageRepository extends MongoRepository<Image, String> {
    void deleteByEventId(String eventId);
    List<Image> findByEventId(String eventId);
}
