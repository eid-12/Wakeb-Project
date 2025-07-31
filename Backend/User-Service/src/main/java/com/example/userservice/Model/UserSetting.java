package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {

    // Primary key, shared with User.id
    @Id
    @Column(name = "user_id")
    private Integer userId;

    // One-to-one relationship with User entity (shared primary key)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ---------- Settings fields ----------

    // Preferred language (default: English)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String language = "English";

    // UI theme preference (default: Light)
    @Builder.Default
    @Column(nullable = false, length = 10)
    private String theme = "Light";

    // Font size preference (default: Small)
    @Builder.Default
    @Column(nullable = false, length = 10)
    private String fontSize = "Small";

    // ---------- Boolean feature flags ----------

    // Whether user wants saved places notifications (default: false)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean savedPlacesNotifications = false;

    // Whether location tracking is enabled (default: true)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "tinyint(1) default 1")
    private boolean locationTracking = true;

    // Whether search history should auto-delete (default: false)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean autoDeleteHistory = false;

    // Whether data should be exported to CSV (default: false)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean exportToCsv = false;

    // Set default values if fields are still null before persisting
    @PrePersist
    void initDefaults() {
        if (language == null)  language  = "English";
        if (theme == null)     theme     = "Light";
        if (fontSize == null)  fontSize  = "Small";
    }
}
