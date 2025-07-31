package com.example.userservice.Repository;

import com.example.userservice.Model.User;
import com.example.userservice.dto.user.UserResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Check if a user exists by username (case-sensitive)
    boolean existsByName(String username);

    // Search for users by partial name (case-insensitive) with pagination
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Check if a user exists with the given email
    boolean existsByEmail(String email);

    // Custom query to return a projection of the user including selected fields
    @Query("""
       SELECT new com.example.userservice.dto.user.UserResponse(
              u.id,
              u.name,
              s.language,
              s.theme,
              s.fontSize,
              COALESCE(s.locationTracking, false),
              u.isUser,
              u.email,
              u.active
       )
       FROM User u
       LEFT JOIN u.settings s
       WHERE u.id = :id
    """)
    Optional<UserResponse> findProjectedById(@Param("id") Integer id);

    // Count all active users
    long countByActiveTrue();

    // Count all active admins (non-user accounts)
    long countByActiveTrueAndIsUserFalse();
}
