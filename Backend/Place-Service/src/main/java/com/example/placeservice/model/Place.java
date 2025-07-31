package com.example.placeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity // Marks this class as a JPA entity mapped to a table
@Data // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: no-argument constructor
@AllArgsConstructor // Lombok: constructor with all fields
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Integer id;

    @Column(length = 100, nullable = false) // Place name (required, max 100 chars)
    private String name;

    @Column(columnDefinition = "TEXT") // Optional long description
    private String description;

    @Column(nullable = false, precision = 9, scale = 6) // Latitude with 6 decimal places
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6) // Longitude with 6 decimal places
    private BigDecimal longitude;

    // Optional image path (e.g. for map thumbnail or photo)
    private String imagePath;

    @CreationTimestamp // Automatically set when inserted into the database
    private Timestamp createdAt;

    // Optional category for the place (e.g., "Restaurant", "Park")
    private String category;

    @Column(name = "user_id", nullable = false) // ID of the user who owns this place
    private Integer userId;
}
