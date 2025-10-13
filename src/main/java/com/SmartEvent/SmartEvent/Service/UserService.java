package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Dto.UserDto;
import com.SmartEvent.SmartEvent.Mappers.UserMapper;
import com.SmartEvent.SmartEvent.Model.User;
import com.SmartEvent.SmartEvent.Repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserDto> getAllUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> users;
        if (search == null || search.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        }

        return users.map(UserMapper::toDto);
    }


    public UserDto getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        return UserMapper.toDto(user);
    }

    public ResponseEntity<String> deleteUserById(String id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("user has been deleted");
    }

    public ResponseEntity<String> updateUserById(String id, UserDto userDto) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new IllegalArgumentException("User not found!");
        }
        user.setUserName(userDto.getUserName());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPhoto(userDto.getPhoto());
        user.setRole(userDto.getRole());
        userRepository.save(user);
        return ResponseEntity.ok("user has been updated");
    }

    public ResponseEntity<String> createUser(String firstName, String lastName, String email, int phoneNumber, String photo, String role, String userName, String password) {
        if (userRepository.findByUsername(userName) == null) {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setUserName(userName);
            user.setPassword(password);
            user.setRole(role);
            user.setPhoto(photo);
            userRepository.save(user);
            return ResponseEntity.ok("user has been created");
        }else {
            throw new IllegalArgumentException("Username already taken!");
    }
}
public String ActivateUser(String id) {
    System.out.println(id);
    User user = userRepository.findById(id).orElse(null);
    System.out.println(user.getUsername());
    if (user == null) {
        throw new IllegalArgumentException("User not found!");
    }
    user.setStatus(true);
    userRepository.save(user);
    return "user has been activated";
}
public String DeactivateUser(String id) {
    System.out.println(id);
    User user = userRepository.findById(id).orElse(null);
    System.out.println(user.getUsername());
    if (user == null) {
        throw new IllegalArgumentException("User not found!");
    }
    user.setStatus(false);
    userRepository.save(user);
    return "user has been deactivated";
}
public ResponseEntity<String> uploadUserPhoto(String id, MultipartFile file) {
    try {
        // 1. Check user existence
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. Create uploads directory if it doesn't exist
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 3. Save file
        String fileName = id + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Save path in DB
        user.setPhoto(filePath.toString());
        userRepository.save(user);

        return ResponseEntity.ok("Photo uploaded successfully");

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
    }
}
public ResponseEntity<Resource> getUserPhoto(String id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    try {
        Path filePath = Paths.get(user.getPhoto());
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG).body(resource);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    } catch (Exception e) {
        return ResponseEntity.notFound().build();
    }
}
    public ResponseEntity<String> deleteUserPhoto(String id) {
        try {
            // 1. Check user existence
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // 2. Check if user has a photo
            if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
                return ResponseEntity.badRequest().body("No photo to delete for this user");
            }

            // 3. Delete file from disk
            Path filePath = Paths.get(user.getPhoto());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // 4. Remove photo path from DB
            user.setPhoto(null);
            userRepository.save(user);

            return ResponseEntity.ok("Photo deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}