package com.example.placeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    // تم تغيير الاسم هنا من imagePath إلى filename ليتوافق مع الـ Controller والـ Service
    private String filename; 

    @CreationTimestamp 
    private Timestamp createdAt;

    private String category;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
}
