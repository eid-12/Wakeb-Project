package com.example.authservice.controller;

import com.example.authservice.client.UserClient;
import com.example.authservice.dto.*;
import com.example.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserClient userClient;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        String token = authService.register(request).token();
        addTokenCookie(response, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        String token = authService.login(request).token();
        addTokenCookie(response, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true) // تم التغيير لـ true للعمل مع HTTPS
                .sameSite("None") // تم التغيير لـ None للعمل عبر البروكسي
                .path("/")
                .maxAge(0)
                .domain("cloudbase.website")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().build();
    }

    // دالة مساعدة لضبط الكوكيز بشكل موحد للإنتاج
    private void addTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 2)
                .secure(true) // تفعيل Secure لأن الموقع يعمل بـ HTTPS عبر Cloudflare
                .sameSite("None") // ضروري جداً لقبول الكوكيز عند استخدام البروكسي
                .domain("cloudbase.website")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntime(RuntimeException ex) {
        return ex.getMessage();
    }

    @PatchMapping("/password")
    public void changePassword(@RequestBody @Valid PasswordChangeRequest dto) {
        authService.changePassword(dto.userId(), dto);
    }

    @DeleteMapping("/dele/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        userClient.deleteUser(id);
        authService.deleteUser(id);
    }

    @PatchMapping("/username")
    public void changeUsername(@RequestBody @Valid UsernameChangeRequest dto) {
        userClient.changeUsername(dto.userId(), dto.newUsername());
        authService.changeUsername(dto.userId(), dto.newUsername());
    }

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
