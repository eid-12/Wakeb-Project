package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER")

public class User {

    // Primary key (ID) of the user
    @Id
    private Integer id;

    // User's name (required, max 50 characters)
    @Column(nullable = false, length = 50)
    private String name;

    // User's email (optional, must be unique, max 100 characters)
    @Column(nullable = true, unique = true, length = 100)
    private String email;

    // Account creation timestamp, default is current time
    @Builder.Default
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    // Whether the user is active (default: true)
    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    // Whether the user is a regular user (not admin) (default: true)
    @Column(columnDefinition = "boolean default true")
    private Boolean isUser = true;

    // One-to-one relationship with the UserSetting entity
    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private UserSetting settings;

    // Utility method to set the user in the settings as well
    public void setSettings(UserSetting st) {
        this.settings = st;
        st.setUser(this); // Ensure bidirectional link is set
    }
}
