package com.example.placeservice.controller;

import com.example.placeservice.dto.PlaceRequest;
import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.model.Place;
import com.example.placeservice.repository.PlaceRepository;
import com.example.placeservice.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.placeservice.repository.PlaceRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController // Marks this class as a REST controller that handles HTTP requests
@RequestMapping("/api/place") // Base URL for all endpoints in this controller
@RequiredArgsConstructor // Lombok: injects PlaceService through constructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceRepository placeRepo;


    // GET /api/place
    // Returns all saved places for the current user (based on userId in header)
    @GetMapping
    public List<PlaceResponse> list(
            @RequestHeader("X-User-Id") int userId
    ) {
        return placeService.list(userId);
    }

    // POST /api/place
    // Adds a new place for the current user
    @PostMapping(value = "/places", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> add(
            @RequestHeader(value = "X-User-Id", required = false) Integer userId,
            @RequestParam("name")        String name,
            @RequestParam("description") String description,
            @RequestParam("category")    String category,
            @RequestParam("latitude")    BigDecimal latitude,
            @RequestParam("longitude")   BigDecimal longitude,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing X-User-Id");
        }

        String filename = null;
        if (image != null && !image.isEmpty()) {
            Path uploadDir = Paths.get(System.getenv()
                    .getOrDefault("RAILWAY_VOLUME_MOUNT_PATH", "/app/uploads"));
            Files.createDirectories(uploadDir);

            filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            image.transferTo(uploadDir.resolve(filename));
        }

        Place place = new Place(
                null,
                name,
                description,
                latitude,
                longitude,
                filename,
                null,
                category,
                userId
        );
        Place saved = placeRepo.save(place);

        // رجّعي الشي اللي تحتاجه الواجهة (مثلاً الـ id)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    // DELETE /api/place/{placeId}
    // Deletes a specific place by its ID for the current user
    @DeleteMapping("/{placeId}")
    public void delete(
            @RequestHeader("X-User-Id") int userId,
            @PathVariable Integer placeId
    ) {
        placeService.delete(userId, placeId);
    }

    // DELETE /api/place
    // Deletes all saved places for the current user
    @DeleteMapping
    public void deleteAll(
            @RequestHeader("X-User-Id") int userId
    ) {
        placeService.deleteAll(userId);
    }
}
