package com.example.savedplaceservice.controller;

import com.example.savedplaceservice.dto.AddSavedPlaceRequest;
import com.example.savedplaceservice.dto.SavedPlaceResponse;
import com.example.savedplaceservice.service.SavedPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST API controller
@RequestMapping("/api/saved") // Base URL for all saved place endpoints
@RequiredArgsConstructor // Lombok: automatically injects the service through constructor
public class SavedPlaceController {

    private final SavedPlaceService service;

    // POST /api/saved
    // Adds a new saved place for the authenticated user
    @PostMapping
    public SavedPlaceResponse add(
            @RequestHeader("X-User-Id") int userId,
            @RequestBody @Valid AddSavedPlaceRequest body
    ) {
        return service.add(userId, body);
    }

    // DELETE /api/saved/{placeId}
    // Removes a saved place by its ID for the current user
    @DeleteMapping("/{placeId}")
    public void remove(
            @RequestHeader("X-User-Id") int userId,
            @PathVariable Integer placeId
    ) {
        service.remove(userId, placeId);
    }

    // GET /api/saved
    // Returns a list of all saved places for the current user
    @GetMapping
    public List<SavedPlaceResponse> list(@RequestHeader("X-User-Id") int userId) {
        return service.list(userId);
    }
}
