package com.example.placeservice.controller;

import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.model.Place;
import com.example.placeservice.repository.PlaceRepository;
import com.example.placeservice.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController 
@RequestMapping("/api/place") 
@RequiredArgsConstructor 
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceRepository placeRepo;

    // جلب جميع الأماكن الخاصة بالمستخدم
    @GetMapping
    public List<PlaceResponse> list(
            @RequestHeader("X-User-Id") int userId
    ) {
        return placeService.list(userId);
    }

    // إضافة مكان جديد مع معالجة الصورة
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
            // استخدام مسار الرفع من متغيرات البيئة
            Path uploadDir = Paths.get(System.getenv()
                    .getOrDefault("UPLOAD_PATH", "/app/uploads"));
            
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

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

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // حذف مكان محدد (يستدعي الخدمة لحذف السجل والصورة معاً)
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-Id") int userId,
            @PathVariable Integer placeId
    ) {
        // تم التغيير لاستدعاء دالة الحذف في الـ Service لضمان حذف الملف
        placeService.delete(userId, placeId);
        return ResponseEntity.noContent().build();
    }

    // حذف جميع أماكن المستخدم
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(
            @RequestHeader("X-User-Id") int userId
    ) {
        placeService.deleteAll(userId);
        return ResponseEntity.noContent().build();
    }
}
