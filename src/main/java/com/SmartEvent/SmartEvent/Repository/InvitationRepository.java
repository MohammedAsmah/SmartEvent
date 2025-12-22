package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends MongoRepository<Invitation, String> {
    List<Invitation> findByEventId(String eventId);
    @Query("{'invite.$id':?0}")
    List<Invitation> findByInviteId(String id);
    Optional<Invitation> findByValidationToken(String verificationToken);
}
