package com.example.searchservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchHistory {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer Id;
    @Column(name="user_id",nullable=false)
    private Integer userId;

    @Column(nullable=false,length=255)
    private String query;


    @Column(nullable=false)
    private Instant searchedAt = Instant.now();
}
