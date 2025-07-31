package com.example.authservice.service;

import com.example.authservice.client.UserClient;
import com.example.authservice.dto.*;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service // Marks this class as a Spring service component
@RequiredArgsConstructor // Automatically injects final dependencies via constructor
@Transactional // Enables transaction management for all methods
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final UserClient userClient;

    // Registers a new user, encodes password, saves to DB, and calls user service
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.username()))
            throw new RuntimeException("User already exists");

        // Create user object and save it
        User user = User.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))
                .isUser(true)
                .build();
        userRepo.save(user);

        // Inform the user-service via Feign client
        userClient.createUser(
                new UserCreateRequest(
                        user.getId(),
                        user.getUsername()
                )
        );

        // Return JWT token as AuthResponse
        return new AuthResponse(jwtService.generateToken(user));
    }

    // Authenticates the user by username and password
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByUsername(req.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Check if password matches
        if (!encoder.matches(req.password(), user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        return new AuthResponse(jwtService.generateToken(user));
    }

    // Allows user to change their password (must provide old password)
    public void changePassword(Integer id, PasswordChangeRequest dto) {
        User u = userRepo.findById(id).orElseThrow();
        if (!encoder.matches(dto.oldPassword(), u.getPassword()))
            throw new IllegalArgumentException("Old password is wrong");

        u.setPassword(encoder.encode(dto.newPassword()));
        userRepo.save(u);
    }

    // Admin resets user password without needing the old one
    public void updatePassword(Integer id, String newPassword) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }

    // Deletes a user by ID
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepo.deleteById(id);
    }

    // Updates a user's username if the new one is not already taken
    @Transactional
    public void changeUsername(Integer userId, String newUsername) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userRepo.existsByUsername(newUsername)) {
            throw new RuntimeException("Username already taken");
        }

        user.setUsername(newUsername);
    }

}
