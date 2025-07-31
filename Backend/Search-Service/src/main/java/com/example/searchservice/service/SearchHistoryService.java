package com.example.searchservice.service;

import com.example.searchservice.dto.SearchHistoryRequest;
import com.example.searchservice.dto.SearchHistoryResponse;
import com.example.searchservice.model.SearchHistory;
import com.example.searchservice.repository.SearchHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional  // All methods in this class are transactional by default
public class SearchHistoryService {

    private final SearchHistoryRepository repo;

    // Add a new search history entry for the user
    @Transactional
    public SearchHistoryResponse add(Integer userId, SearchHistoryRequest req) {
        SearchHistory h = new SearchHistory();
        h.setUserId(userId);
        h.setQuery(req.query());
        return map(repo.save(h)); // Save to DB and return mapped response
    }

    // Get all search history entries for the user, ordered by time (latest first)
    @Transactional(readOnly = true)
    public List<SearchHistoryResponse> list(Integer userId) {
        return repo.findByUserIdOrderBySearchedAtDesc(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    // Remove a single search history entry by entryId for the given user
    @Transactional
    public void removeOne(Integer userId, Integer entryId) {
        repo.deleteByIdAndUserId(entryId, userId);
    }

    // Remove all search history entries for the given user
    @Transactional
    public void clearAll(Integer userId) {
        repo.deleteByUserId(userId);
    }

    // Utility method to convert entity to DTO
    private SearchHistoryResponse map(SearchHistory h) {
        return new SearchHistoryResponse(
                h.getId(),
                h.getQuery(),
                h.getSearchedAt()
        );
    }
}
