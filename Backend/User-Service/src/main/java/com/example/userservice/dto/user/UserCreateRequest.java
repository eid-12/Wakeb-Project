package com.example.userservice.dto.user;

import java.util.Set;

public record UserCreateRequest(
        Integer id,
        String  username

) {

}
