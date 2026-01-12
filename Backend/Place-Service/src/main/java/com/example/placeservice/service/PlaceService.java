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
        placeRepo.deleteByUserId(userId);
    }

    @Transactional
    public void delete(Integer userId, Integer placeId) {
        // 1. جلب الكيان أولاً (هنا قد يحدث الخطأ إذا لم تكن الدالة في الـ Repo)
        Place place = placeRepo.findByIdAndUserId(placeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        // 2. منطق حذف الصورة
        String uploadPath = System.getenv().getOrDefault("UPLOAD_PATH", "/app/uploads");
        String filename = place.getFilename();

        if (filename != null && !filename.isEmpty()) {
            try {
                Path filePath = Paths.get(uploadPath).resolve(filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }

        // 3. حذف السجل
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
