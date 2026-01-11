package com.example.placeservice.service;

import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.model.Place;
import com.example.placeservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// الاستيرادات الضرورية للتعامل مع الملفات وحل مشكلة الـ Build
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service 
@RequiredArgsConstructor 
public class PlaceService {

    private final PlaceRepository placeRepo;

    // استرجاع جميع الأماكن لمستخدم معين
    @Transactional(readOnly = true)
    public List<PlaceResponse> list(Integer userId) {
        return placeRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    // حذف جميع الأماكن لمستخدم معين
    @Transactional
    public void deleteAll(Integer userId) {
        // ملاحظة: إذا أردت حذف ملفات الصور عند حذف الكل، ستحتاج لاستدعاء دالة الحذف الفردية لكل عنصر
        placeRepo.deleteByUserId(userId);
    }

    // حذف مكان محدد مع حذف الصورة التابعة له من المجلد
    @Transactional
    public void delete(Integer userId, Integer placeId) {
        // 1. جلب بيانات المكان قبل الحذف لمعرفة اسم الملف
        Place place = placeRepo.findByIdAndUserId(placeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found for this user"));

        // 2. تحديد مسار المجلد من متغيرات البيئة (مطابق للـ Controller)
        String uploadPath = System.getenv().getOrDefault("UPLOAD_PATH", "/app/uploads");
        String filename = place.getFilename();

        // 3. حذف الملف الفعلي من المجلد إذا كان موجوداً
        if (filename != null && !filename.isEmpty()) {
            try {
                Path filePath = Paths.get(uploadPath).resolve(filename);
                // حذف الملف من القرص (Volume) لضمان عدم تراكم الصور
                Files.deleteIfExists(filePath); 
            } catch (IOException e) {
                // تسجيل الخطأ في السجلات إذا فشل الحذف الفيزيائي للملف
                System.err.println("Failed to delete image file: " + filename + " Error: " + e.getMessage());
            }
        }

        // 4. حذف السجل من قاعدة البيانات بعد التأكد من معالجة الملف
        placeRepo.delete(place);
    }

    // تحويل الكيان (Entity) إلى DTO للعرض
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
