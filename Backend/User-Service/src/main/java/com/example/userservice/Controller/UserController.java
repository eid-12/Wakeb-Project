package com.example.userservice.Controller;

import com.example.userservice.Model.User;
import com.example.userservice.Repository.UserRepository;
import com.example.userservice.Service.UserService;
import com.example.userservice.dto.user.EmailUpdateRequest;
import com.example.userservice.dto.user.UserCreateRequest;
import com.example.userservice.dto.user.UserListWithStats;
import com.example.userservice.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    // ✅ Create a new user
    @PostMapping("/creat") // ⚠️ Typo: consider changing to "/create"
    public void create(@RequestBody UserCreateRequest request) {
        userService.createUser(request);
    }

    // ✅ Change a user's username by their ID
    @PutMapping("/{id}/username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUsername(@PathVariable Integer id,
                               @RequestParam String username) {
        userService.changeUsername(id, username);
    }

    // ✅ Admin-only: Get paginated user list + active user/admin stats
    @PreAuthorize("!authentication.principal.isUser")
    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserListWithStats list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {

        Page<UserResponse> users = userService.listUsers(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "active"))
        );
        long activeUserCount = userService.countActiveUsers();
        long activeAdminCount = userService.countActiveAdmins();

        return new UserListWithStats(users, activeUserCount, activeAdminCount);
    }

    // ✅ Admin-only: Toggle user role between USER/ADMIN
    @PreAuthorize("!authentication.principal.isUser")
    @PatchMapping("/admin/role/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeRole(@PathVariable Integer id) {
        userService.setRole(id);
    }

    // ✅ Activate or deactivate a user account
    @PutMapping("/{id}")
    public void toggleActive(@PathVariable Integer id) {
        userService.setActive(id);
    }

    // ✅ Get current logged-in user's data by ID from header
    @GetMapping
    public UserResponse findOne(@RequestHeader("X-User-Id") int userId) {
        return userService.findById(userId);
    }

    // ✅ Update email address for the current logged-in user
    @PatchMapping("/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeEmail(@RequestHeader("X-User-Id") Integer userId,
                            @RequestBody @Valid EmailUpdateRequest body) {
        userService.changeEmail(userId, body);
    }
}
