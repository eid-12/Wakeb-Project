package com.example.placeservice.repository;

import com.example.placeservice.model.Place;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

// JPA repository for managing Place entities
public interface PlaceRepository extends JpaRepository<Place, Integer> {

    // Finds all places for a specific user, ordered by creation time (newest first)
    List<Place> findByUserIdOrderByCreatedAtDesc(Integer userId);
    void deleteByUserId(Integer userId);
    // Finds a specific place by its ID and the user who owns it
    Optional<Place> findByIdAndUserId(Integer id, Integer userId);

    // Deletes all places for a specific user, returns number of rows deleted
    Long deleteByUserId(Integer userId);

    // Deletes a specific place for a specific user, returns number of rows deleted
    Long deleteByIdAndUserId(Integer id, Integer userId);
}
