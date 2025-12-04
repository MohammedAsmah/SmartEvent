package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface InvitationRepository extends MongoRepository<Invitation, String> {
    List<Invitation> findByEventId(String eventId);
    List<Invitation> findByInviteId(String userId);
}
