package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Model.Image;
import com.SmartEvent.SmartEvent.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public List<Image> getImagesByEvent(String eventId) {
        return imageRepository.findByEventId(eventId);
    }

    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public void deleteImagesByEvent(String eventId) {
        imageRepository.deleteByEventId(eventId);
    }
}
