package com.SmartEvent.SmartEvent.Repository;

import com.SmartEvent.SmartEvent.Model.Role;
import com.SmartEvent.SmartEvent.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
