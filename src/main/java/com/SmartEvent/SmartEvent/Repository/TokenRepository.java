package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByUsername(String username);
    void deleteByUsername(String username);
}
