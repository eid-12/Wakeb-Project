package com.example.searchservice.dto;


import java.time.Instant;

public record SearchHistoryResponse(Integer id,String query,Instant searchedAt) {}
