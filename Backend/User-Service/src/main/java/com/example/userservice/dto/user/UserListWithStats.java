package com.example.userservice.dto.user;

import lombok.Data;
import org.springframework.data.domain.Page;
@Data
public class UserListWithStats {
    private Page<UserResponse> users;
    private long activeUserCount;
    private long activeAdminCount;

    // Constructors
    public UserListWithStats(Page<UserResponse> users, long activeUserCount, long activeAdminCount) {
        this.users = users;
        this.activeUserCount = activeUserCount;
        this.activeAdminCount = activeAdminCount;
    }

}

