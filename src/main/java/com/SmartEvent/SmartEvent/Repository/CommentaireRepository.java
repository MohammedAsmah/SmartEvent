package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Commentaire;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentaireRepository extends MongoRepository<Commentaire, String> {
}
