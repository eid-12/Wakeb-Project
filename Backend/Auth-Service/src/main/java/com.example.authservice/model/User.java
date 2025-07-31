package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity // Marks this class as a JPA entity (maps to a table)
@Table(name = "users") // Specifies the database table name
@Data // Lombok: generates getters/setters, equals, hashCode, toString
@Builder // Lombok: enables the builder pattern
@NoArgsConstructor // Lombok: generates no-arg constructor
@AllArgsConstructor // Lombok: generates all-args constructor
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Integer id;

    @Column(unique = true, nullable = false, length = 50) // Username must be unique and not null
    private String username;

    @Column(nullable = false, length = 60) // Password must not be null
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true") // Flag to indicate user role
    private Boolean isUser = true;

    /* ------------ UserDetails Implementation ------------ */

    // Return user authorities (roles)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return isUser
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"))
                : List.of(); // Can be extended for ROLE_ADMIN, etc.
    }

    @Override public String  getPassword()             { return password; }
    @Override public String  getUsername()             { return username; }
    @Override public boolean isAccountNonExpired()     { return true; } // Account never expires
    @Override public boolean isAccountNonLocked()      { return true; } // Account is never locked
    @Override public boolean isCredentialsNonExpired() { return true; } // Credentials never expire
    @Override public boolean isEnabled()               { return true; } // Account is always enabled
}
