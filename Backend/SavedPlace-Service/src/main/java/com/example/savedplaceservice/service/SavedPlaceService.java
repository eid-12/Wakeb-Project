package com.example.savedplaceservice.service;

import com.example.savedplaceservice.dto.AddSavedPlaceRequest;
import com.example.savedplaceservice.dto.SavedPlaceResponse;
import com.example.savedplaceservice.model.SavedPlace;
import com.example.savedplaceservice.repository.SavedPlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: automatically generates constructor with final fields
public class SavedPlaceService {

    private final SavedPlaceRepository repo;

    // -------- Add new saved place for the user --------
    @Transactional
    public SavedPlaceResponse add(Integer userId, AddSavedPlaceRequest req) {
        // Prevent duplicates based on userId and title
        if (repo.existsByUserIdAndTitle(userId, req.title())) {
            return null; // Already saved
        }

        // Create new place entity and fill data
        SavedPlace place = new SavedPlace();
        place.setUserId(userId);
        place.setTitle(req.title());
        place.setLatitude(req.latitude());
        place.setLongitude(req.longitude());

        // Save to database and return the mapped response
        return map(repo.save(place));
    }

    // -------- Remove a saved place by ID --------
    @Transactional
    public void remove(Integer userId, Integer placeId) {
        // Check if place exists for this user
        SavedPlace place = repo.findByIdAndUserId(placeId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Place not found"));

        // Delete from DB
        repo.delete(place);
    }

    // -------- List all saved places for the user --------
    @Transactional(readOnly = true)
    public List<SavedPlaceResponse> list(Integer userId) {
        // Find all saved places for user, and convert them to DTOs
        return repo.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    // -------- Utility method to convert entity to DTO --------
    private SavedPlaceResponse map(SavedPlace p) {
        return new SavedPlaceResponse(
                p.getId(),
                p.getTitle(),
                p.getLatitude(),
                p.getLongitude(),
                p.getCreatedAt()
        );
    }
}
