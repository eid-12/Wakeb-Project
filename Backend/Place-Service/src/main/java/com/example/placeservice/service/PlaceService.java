package com.example.placeservice.service;

import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.model.Place;
import com.example.placeservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// الاستيرادات التي غالباً ما تسبب فشل الـ Build إذا نقصت
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional; 

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepo;

    @Transactional(readOnly = true)
    public List<PlaceResponse> list(Integer userId) {
        return placeRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public void deleteAll(Integer userId) {
        placeRepo.deleteByUserId(userId);
    }

    @Transactional
    public void delete(Integer userId, Integer placeId) {
        // البحث عن المكان أولاً
        Optional<Place> placeOptional = placeRepo.findByIdAndUserId(placeId, userId);
        
        if (placeOptional.isPresent()) {
            Place place = placeOptional.get();
            String filename = place.getFilename();

            // حذف الملف الفيزيائي
            if (filename != null && !filename.isEmpty()) {
                try {
                    String uploadPath = System.getenv().getOrDefault("UPLOAD_PATH", "/app/uploads");
                    Path filePath = Paths.get(uploadPath).resolve(filename);
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("Failed to delete file: " + filename);
                }
            }
            // حذف السجل من قاعدة البيانات
            placeRepo.delete(place);
        } else {
            throw new IllegalArgumentException("Place not found for this user");
        }
    }

    private PlaceResponse map(Place p) {
        return new PlaceResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getLatitude(), p.getLongitude(), p.getCreatedAt(),
                p.getCategory(), p.getUserId()
        );
    }
}
