package com.example.searchservice.repository;

import com.example.searchservice.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {
    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(Integer userId);
    void deleteByIdAndUserId(Integer id,Integer userId);
    void deleteByUserId(Integer userId);
}
