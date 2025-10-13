package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Invite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InviteRepository extends MongoRepository<Invite, String> {

}
