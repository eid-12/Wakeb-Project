package com.example.placeservice.repository;

import com.example.placeservice.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    // استرجاع الأماكن حسب المستخدم
    List<Place> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // البحث عن مكان محدد لمستخدم محدد
    Optional<Place> findByIdAndUserId(Integer id, Integer userId);

    // دالة واحدة فقط لحذف جميع أماكن المستخدم (تعيد عدد الصفوف المحذوفة)
    Long deleteByUserId(Integer userId);

    // حذف مكان محدد لمستخدم محدد
    Long deleteByIdAndUserId(Integer id, Integer userId);
}
