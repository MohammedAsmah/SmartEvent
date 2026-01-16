package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Invite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InviteRepository extends MongoRepository<Invite, String> {
    @Query("{ 'eventId': ?0, 'personId': ?1 }")
    Optional<Invite> findByEventIdAndPersonId(String eventId, String personId);
    List<Invite> findByEventId(String eventId);

}
