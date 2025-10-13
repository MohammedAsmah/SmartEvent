package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Anotation.RequirePermission;
import com.SmartEvent.SmartEvent.Dto.UserDto;
import com.SmartEvent.SmartEvent.Enums.Permission;
import com.SmartEvent.SmartEvent.Model.User;
import com.SmartEvent.SmartEvent.Service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/Users")
public class UserController {
    UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("")
    @RequirePermission({Permission.WRITE_USER})
    public ResponseEntity<?> addUser(@RequestBody UserDto user) {
        return (userService.createUser(user.getFirstName(),user.getLastName(),user.getEmail(),user.getPhoneNumber(),user.getPhoto(),user.getRole(),user.getUserName(),user.getPassword()));
    }

    @GetMapping("/")
    @RequirePermission({Permission.READ_USER})
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(userService.getAllUsers(search, page, size));
    }

    @GetMapping("{id}")
    @RequirePermission({Permission.READ_USER})
    public UserDto getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }
    @DeleteMapping("{id}")
    @RequirePermission({Permission.DELETE_USER})
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return (userService.deleteUserById(id));
    }

    @PutMapping("{id}")
    @RequirePermission({Permission.UPDATE_USER})
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserDto userDto) {
        return (userService.updateUserById(id, userDto));
    }

    @PostMapping("/avtivate/{id}")
    @RequirePermission({Permission.UPDATE_USER})
    public ResponseEntity<?> avtivateUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.ActivateUser(id));
    }
    @PostMapping("/Deactivate/{id}")
    @RequirePermission({Permission.UPDATE_USER})
    public ResponseEntity<?> deactivateUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.DeactivateUser(id));
    }
    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<String> uploadUserPhoto(
            @PathVariable String id,
            @RequestParam("photo") MultipartFile file) {
        return (userService.uploadUserPhoto(id, file));
    }
    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getUserPhoto(@PathVariable String id) {
        return userService.getUserPhoto(id);
    }
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<?> deleteUserPhoto(@PathVariable String id) {
        return (userService.deleteUserPhoto(id));
    }
}
