package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Geust;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GeustRepository extends MongoRepository<Geust,String> {
Geust findByEmail(String email);
}
