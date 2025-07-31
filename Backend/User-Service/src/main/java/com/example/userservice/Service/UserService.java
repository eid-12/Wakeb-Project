package com.example.userservice.Service;

import com.example.userservice.dto.user.*;
import com.example.userservice.Model.User;
import com.example.userservice.Model.UserSetting;
import com.example.userservice.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    // Create a new user with default settings
    public void createUser(UserCreateRequest dto) {
        User user = User.builder()
                .id(dto.id())
                .name(dto.username())
                .active(true)
                .isUser(true)
                .build();

        UserSetting setting = UserSetting.builder().build();
        user.setSettings(setting); // Link settings to user

        repo.save(user);
    }

    // Get user details by ID using projection (for frontend display)
    @Transactional(readOnly = true)
    public UserResponse findById(Integer id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user id");

        return repo.findProjectedById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));
    }

    // Count active users (users with active = true)
    public long countActiveUsers() {
        return repo.countByActiveTrue();
    }

    // Count active admins (users with active = true and isUser = false)
    public long countActiveAdmins() {
        return repo.countByActiveTrueAndIsUserFalse();
    }

    // Return paginated list of users mapped to UserResponse DTO
    public Page<UserResponse> listUsers(Pageable pageable) {
        return repo.findAll(pageable)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getSettings().getLanguage(),
                        user.getSettings().getTheme(),
                        user.getSettings().getFontSize(),
                        user.getSettings().isLocationTracking(),
                        user.getIsUser(),
                        user.getEmail(),
                        user.getActive()
                ));
    }

    // Toggle active status (enable/disable account)
    public void setActive(Integer id) {
        User u = repo.findById(id).orElseThrow();
        boolean current = Boolean.TRUE.equals(u.getActive());
        u.setActive(!current); // Toggle active status
        repo.save(u);
    }

    // Change user's email with optional verification
    public void changeEmail(Integer id, EmailUpdateRequest req) {
        User user = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (repo.existsByEmail(req.newEmail()))
            throw new IllegalArgumentException("E‑mail already in use");

        String current = user.getEmail();

        // If no email set yet, assign new one
        if (current == null || current.isBlank()) {
            user.setEmail(req.newEmail());
            return;
        }

        // Validate old email before updating
        if (!Objects.equals(current, req.oldEmail()))
            throw new IllegalArgumentException("Old e‑mail does not match our records");

        user.setEmail(req.newEmail());
    }

    // Change user's username after checking for duplicates
    @Transactional
    public void changeUsername(Integer id, String username) {
        User u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (repo.existsByName(username))
            throw new RuntimeException("Username already taken");

        u.setName(username);
    }

    // Toggle user's role between "user" and "admin"
    @Transactional
    public void setRole(Integer id) {
        User u = repo.findById(id).orElseThrow();
        boolean current = Boolean.TRUE.equals(u.getIsUser());
        u.setIsUser(!current); // Toggle role
        repo.save(u);
    }
}
