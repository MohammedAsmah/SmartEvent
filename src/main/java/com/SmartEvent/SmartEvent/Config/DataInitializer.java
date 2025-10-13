package com.SmartEvent.SmartEvent.Config;

import com.SmartEvent.SmartEvent.Enums.Permission;
import com.SmartEvent.SmartEvent.Model.Role;
import com.SmartEvent.SmartEvent.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create ADMIN role
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setPermission(Set.of(
                    Permission.READ_USER,
                    Permission.WRITE_USER,
                    Permission.DELETE_USER,
                    Permission.UPDATE_USER
            ));
            adminRole.setIsSystemRole(true);
            roleRepository.save(adminRole);
        }

        // Create USER role
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setPermission(Set.of(Permission.READ_USER));
            userRole.setIsSystemRole(false);
            roleRepository.save(userRole);
        }
    }
}