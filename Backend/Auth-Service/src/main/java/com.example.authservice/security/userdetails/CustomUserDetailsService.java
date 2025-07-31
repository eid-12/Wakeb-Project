package com.example.authservice.security.userdetails;

import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Marks this class as a Spring service component
@RequiredArgsConstructor // Automatically generates a constructor for final fields
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    // Loads a user by username for Spring Security authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username from the database
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Throw if not found
    }
}
