package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Model.Image;
import com.SmartEvent.SmartEvent.Service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/events/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload/{eventId}")
    public ResponseEntity<Image> uploadImage(@PathVariable String eventId,
                                             @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        // Save file locally (you can change to cloud later)
        Path path = Paths.get("uploads/" + fileName);
        Files.write(path, file.getBytes());

        String url = "uploads/" + fileName; // local URL
        Image image = new Image(url, eventId);

        return ResponseEntity.ok(imageService.saveImage(image));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<Image>> getImagesByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(imageService.getImagesByEvent(eventId));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteImagesByEvent(@PathVariable String eventId) {
        imageService.deleteImagesByEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
