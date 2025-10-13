package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Dto.UserDto;
import com.SmartEvent.SmartEvent.Mappers.UserMapper;
import com.SmartEvent.SmartEvent.Model.Role;
import com.SmartEvent.SmartEvent.Model.User;
import com.SmartEvent.SmartEvent.Repository.RoleRepository;
import com.SmartEvent.SmartEvent.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class RoleSerivce {
    RoleRepository roleRepository;
    public RoleSerivce(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
        }


    public Role getRole(String id) {
        Role role = roleRepository.findById(id).orElse(null);
        return role;
    }

    public ResponseEntity<String> deleteRole(String id) {
        roleRepository.deleteById(id);
        return ResponseEntity.ok("role has been deleted");
    }

    public ResponseEntity<String> updateUserById(String id, Role role) {
        Role rl = roleRepository.findById(id).orElse(null);
        if(role == null){
            throw new IllegalArgumentException("User not found!");
        }
        rl.setName(role.getName());
        rl.setPermission(role.getPermission());
        rl.setIsSystemRole(role.getIsSystemRole());
        roleRepository.save(rl);
        return ResponseEntity.ok("role has been updated");
    }

    public ResponseEntity<String> createRole(Role role) {
        if (roleRepository.findByName(role.getName()) == null) {
            roleRepository.save(role);
            return ResponseEntity.ok("user has been created");
        }else {
            throw new IllegalArgumentException("Username already taken!");
        }
    }
}
