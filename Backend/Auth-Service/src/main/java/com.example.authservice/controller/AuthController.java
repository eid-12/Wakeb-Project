
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

import feign.FeignException;

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



    /** User-service (Feign) failures — do not leak HTTP bodies or stack traces. */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeign(FeignException ex) {
        log.error("User-service error status={} body={}", ex.status(), ex.contentUTF8(), ex);
        String message = switch (ex.status()) {
            case 401, 403 ->
                    "Registration could not finish (profile service rejected the request). Please try again later or contact support.";
            case 404 ->
                    "Registration service endpoint was not found. Please contact support.";
            case 409 ->
                    "This account already exists. Try signing in instead.";
            default ->
                    "We could not finish setting up your profile. Please try again in a few minutes.";
        };
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }

    /** DB constraint / column issues — never expose SQL to clients. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message",
                "We could not create your account. Please try again or pick a different username."));
    }

    /**
     * Known safe messages pass through; JDBC/Hibernate/SQL text is hidden.
     * Walks the cause chain so wrapped {@link DataIntegrityViolationException}s are handled.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        DataIntegrityViolationException div = findInCause(ex, DataIntegrityViolationException.class);
        if (div != null) {
            return handleDataIntegrity(div);
        }

        String raw = deepestMessage(ex);
        if ("User already exists".equals(raw)) {
            return ResponseEntity.badRequest().body(Map.of("message", raw));
        }
        if (raw == null || looksLikeInfrastructureError(raw)) {
            log.error("Registration/auth error", ex);
            return ResponseEntity.badRequest().body(Map.of("message", registrationHint(raw)));
        }
        return ResponseEntity.badRequest().body(Map.of("message", raw));
    }

    private static String registrationHint(String raw) {
        if (raw == null) {
            return "Something went wrong. Please try again later.";
        }
        String m = raw.toLowerCase();
        if (m.contains("could not execute")
                || m.contains("field '")
                || m.contains("doesn't have a default value")) {
            return "We could not save your account (server data configuration). Please try again later or contact support.";
        }
        return "Something went wrong. Please try again later.";
    }

    private static <T extends Throwable> T findInCause(Throwable ex, Class<T> type) {
        Throwable t = ex;
        while (t != null) {
            if (type.isInstance(t)) {
                return type.cast(t);
            }
            t = t.getCause();
        }
        return null;
    }

    /** Prefer the deepest non-blank message (often the JDBC root cause). */
    private static String deepestMessage(Throwable ex) {
        String best = ex.getMessage();
        Throwable t = ex.getCause();
        while (t != null) {
            if (t.getMessage() != null && !t.getMessage().isBlank()) {
                best = t.getMessage();
            }
            Throwable next = t.getCause();
            if (next == t) {
                break;
            }
            t = next;
        }
        return best;
    }

    private static boolean looksLikeInfrastructureError(String msg) {
        String m = msg.toLowerCase();
        return m.contains("could not execute")
                || m.contains("java.sql.")
                || m.contains("sqlexception")
                || m.contains("jdbc")
                || m.contains("hibernate")
                || m.contains("constraint")
                || m.contains("duplicate entry")
                || (m.contains("field '") && m.contains("default"));
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

