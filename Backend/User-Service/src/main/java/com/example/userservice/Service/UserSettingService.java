package com.example.userservice.Service;


import com.example.userservice.Repository.UserSettingRepository;
import com.example.userservice.dto.user.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSettingService {
    private final UserSettingRepository userSettingRepository;



}
