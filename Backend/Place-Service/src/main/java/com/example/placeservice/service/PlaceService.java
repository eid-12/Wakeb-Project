package com.example.placeservice.service;

import com.example.placeservice.dto.PlaceRequest;
import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.model.Place;
import com.example.placeservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.io.IOException;            // للتعامل مع أخطاء المدخلات والمخرجات
import java.nio.file.Files;            // لحذف الملفات فعلياً
import java.nio.file.Path;             // للتعامل مع مسارات الملفات
import java.nio.file.Paths;            // لتحويل النصوص إلى مسارات
import java.util.Optional;             // إذا كنت تستخدم Optional بشكل صريح

@Service // Marks this class as a Spring service component
@RequiredArgsConstructor // Lombok: auto-generates constructor for final fields
public class PlaceService {

    private final PlaceRepository placeRepo;

//    // Adds a new place for the specified user
//    public PlaceResponse add(Integer userId, PlaceRequest req) {
//        Place place = new Place(
//                null,                 // ID will be auto-generated
//                req.name(),
//                req.description(),
//                req.latitude(),
//                req.longitude(),
//                null,
//                null,                // createdAt will be auto-generated
//                req.category(),
//                userId
//        );
//
//        Place saved = placeRepo.save(place); // Save to database
//        return map(saved); // Return response DTO
//    }

    // Retrieves all places for a specific user (ordered by creation date)
    @Transactional(readOnly = true)
    public List<PlaceResponse> list(Integer userId) {
        return placeRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::map) // Convert each Place to PlaceResponse
                .toList();
    }

    // Deletes all places for the given user
    @Transactional
    public void deleteAll(Integer userId) {
        placeRepo.deleteByUserId(userId);
    }

    // Deletes a specific place by ID and user
    @Transactional
 public void delete(Integer userId, Integer placeId) {

        Long deleted = placeRepo.deleteByIdAndUserId(placeId, userId);

        if (deleted == 0) {

            throw new IllegalArgumentException("Place not found for this user");

        }}

    // Converts a Place entity to a PlaceResponse DTO
    private PlaceResponse map(Place p) {
        return new PlaceResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getLatitude(),
                p.getLongitude(),
                p.getCreatedAt(),
                p.getCategory(),
                p.getUserId()
        );
    }
}
