package com.example.userservice.Controller;

import com.example.userservice.Service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor

public class UserSettingController {
    private final UserSettingService userSettingService;
}
