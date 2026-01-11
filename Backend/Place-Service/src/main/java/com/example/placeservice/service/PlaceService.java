package com.example.placeservice.service;

import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.model.Place;
import com.example.placeservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        // حذف السجلات من القاعدة
        placeRepo.deleteByUserId(userId);
    }

    @Transactional
    public void delete(Integer userId, Integer placeId) {
        // 1. جلب البيانات لمعرفة اسم الملف قبل حذفه
        Place place = placeRepo.findByIdAndUserId(placeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found for this user"));

        // 2. مسار الصور
        String uploadPath = System.getenv().getOrDefault("UPLOAD_PATH", "/app/uploads");
        String filename = place.getFilename();

        // 3. حذف الملف من القرص
        if (filename != null && !filename.isEmpty()) {
            try {
                Path filePath = Paths.get(uploadPath).resolve(filename);
                Files.deleteIfExists(filePath); 
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }

        // 4. حذف السجل النهائي
        placeRepo.delete(place);
    }

    private PlaceResponse map(Place p) {
        return new PlaceResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getLatitude(), p.getLongitude(), p.getCreatedAt(),
                p.getCategory(), p.getUserId()
        );
    }
}
