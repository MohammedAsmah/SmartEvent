package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Token;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token>  findByUsername(String username);
    Long deleteByUsername(@Param("username") String username);
}
