
package com.example.authservice.controller;

// Import required classes
import com.example.authservice.client.UserClient;
import com.example.authservice.dto.*;
import com.example.authservice.security.userdetails.CustomUserDetailsService;
import com.example.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// REST controller that handles authentication-related endpoints
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Injects required dependencies via constructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserClient userClient;

    // Register a new user and set JWT token as an HTTP-only cookie
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        String token = authService.register(request).token();

        // Create a secure HTTP-only cookie to store the token
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 2)
                .secure(false)// Change to true in production
                .sameSite("Strict")
                .domain("cloudbase.website")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().build();
    }

    // Login a user and return a token as an HTTP-only cookie
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        String token = authService.login(request).token(); // Get only the token




        ResponseCookie cookie = ResponseCookie.from("token", token)

                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 2)
                .secure(false)// Change to true in production
                .sameSite("Strict")
                .domain("cloudbase.website")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // يحذف فوراً


                .domain("cloudbase.website")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().build();
    }



    /** DB constraint / column issues — never expose SQL to clients. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message",
                "We could not create your account. Please try again or pick a different username."));
    }

    /** Safe messages for known business errors; hide internal/DB details for anything else. */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String raw = ex.getMessage();
        if (raw == null || looksLikeInfrastructureError(raw)) {
            log.error("Unhandled registration/auth error", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Something went wrong. Please try again later."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", raw));
    }

    private static boolean looksLikeInfrastructureError(String msg) {
        String m = msg.toLowerCase();
        return m.contains("sql")
                || m.contains("jdbc")
                || m.contains("hibernate")
                || m.contains("could not execute")
                || m.contains("constraint")
                || m.contains("field '")
                || m.contains("column ");
    }

    // Change password by user
    @PatchMapping("/password")
    public void changePassword(@RequestBody @Valid PasswordChangeRequest dto) {
        authService.changePassword(dto.userId(), dto);
    }

    // Delete a user from both the auth service and user service
    @DeleteMapping("/dele/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        userClient.deleteUser(id);
        authService.deleteUser(id);
    }

    // Change username in both auth and user services
    @PatchMapping("/username")
    public void changeUsername(@RequestBody @Valid UsernameChangeRequest dto) {
        userClient.changeUsername(dto.userId(), dto.newUsername());
        authService.changeUsername(dto.userId(), dto.newUsername());
    }

    // Admin resets a user's password
    @PatchMapping("/admin/users/{id}")
    public ResponseEntity<?> resetPasswordByAdmin(@PathVariable Integer id,
                                                  @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        authService.updatePassword(id, newPassword);

        return ResponseEntity.ok("Password updated");
    }
}

