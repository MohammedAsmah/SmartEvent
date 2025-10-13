package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Model.Role;
import com.SmartEvent.SmartEvent.Model.User;
import com.SmartEvent.SmartEvent.Repository.RoleRepository;
import com.SmartEvent.SmartEvent.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Load user's role and permissions
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null && !user.getRole().isEmpty()) {
            Role role = roleRepository.findById(user.getRole()).orElse(null);
            if (role != null) {
                // Add role as authority
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

                // Add individual permissions as authorities
                if (role.getPermission() != null) {
                    role.getPermission().forEach(permission ->
                            authorities.add(new SimpleGrantedAuthority(permission.name())));
                }
            }
        }

        // Create UserDetails with authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isStatus(), // account enabled
                true, // account not expired
                true, // credentials not expired
                true, // account not locked
                authorities
        );
    }
}