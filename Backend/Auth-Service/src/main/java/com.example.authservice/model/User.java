package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isUser = true;

    /**
     * Required by existing MySQL schema (NOT NULL, no DEFAULT). Not shown in UI — login/register stay
     * username + password only. Hibernate must include this column in INSERT or MySQL rejects the row.
     */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Matches MySQL column {@code is_active} (NOT NULL in many schemas). New accounts are active.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /** Required when MySQL {@code created_at} is NOT NULL without DEFAULT. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void applySignupDefaults() {
        if (isUser == null) {
            isUser = true;
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return isUser
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"))
                : List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}
