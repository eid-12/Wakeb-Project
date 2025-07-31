package com.example.savedplaceservice.repository;

import com.example.savedplaceservice.model.SavedPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Integer> {
    List<SavedPlace> findByUserId(Integer userId);
    Optional<SavedPlace> findByIdAndUserId(Integer id, Integer userId);
    boolean existsByUserIdAndTitle(Integer userId, String title);

}
