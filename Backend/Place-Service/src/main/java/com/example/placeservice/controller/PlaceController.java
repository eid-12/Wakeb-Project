package com.example.placeservice.controller;

import com.example.placeservice.dto.PlaceRequest;
import com.example.placeservice.dto.PlaceResponse;
import com.example.placeservice.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST controller that handles HTTP requests
@RequestMapping("/api/place") // Base URL for all endpoints in this controller
@RequiredArgsConstructor // Lombok: injects PlaceService through constructor
public class PlaceController {

    private final PlaceService placeService;

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
    @PostMapping
    public PlaceResponse add(
            @RequestHeader("X-User-Id") int userId,
            @RequestBody @Valid PlaceRequest req
    ) {
        return placeService.add(userId, req);
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
