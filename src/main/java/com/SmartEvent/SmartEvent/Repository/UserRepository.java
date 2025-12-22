package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // You can add custom queries here
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetCode(String resetCode);
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);

}
