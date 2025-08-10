package com.example.searchservice.controller;

import com.example.searchservice.dto.SearchHistoryRequest;
import com.example.searchservice.dto.SearchHistoryResponse;
import com.example.searchservice.service.SearchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")  // All endpoints in this controller will be prefixed with /api/search
@RequiredArgsConstructor        // Lombok will generate a constructor with required arguments
@Validated                     // Enables validation for @RequestBody, @PathVariable, etc.
public class SearchHistoryController {

    private final SearchHistoryService service;

    // Add a new search history entry for a user
    @PostMapping
    public SearchHistoryResponse add(
            @RequestHeader(value = "X-User-Id", required = false) Integer userId,
            @RequestBody @Valid SearchHistoryRequest body
    ) {
        if (userId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Missing X-User-Id header"
            );
        }
        return service.add(userId, body);
    }
    // Delete a specific search history entry by ID
    @DeleteMapping("/{entryId}")
    public void removeOne(@RequestHeader("X-User-Id") int userId,
                          @PathVariable Integer entryId) {
        service.removeOne(userId, entryId);
    }

    // Delete all search history entries for the user
    @DeleteMapping
    public void clearAll(@RequestHeader("X-User-Id") int userId) {
        service.clearAll(userId);
    }

    // List all search history entries for the user
    @GetMapping
    public List<SearchHistoryResponse> list(@RequestHeader("X-User-Id") int userId) {
        return service.list(userId);
    }
}
