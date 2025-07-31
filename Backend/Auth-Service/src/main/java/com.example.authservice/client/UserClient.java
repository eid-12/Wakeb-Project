package com.example.authservice.client;

// Importing necessary classes
import com.example.authservice.dto.UserCreateRequest;
import com.example.authservice.security.FeignBasicAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// This interface defines a Feign client used to communicate with the user-service
@FeignClient(
        value = "user-service", // Logical name of the service
        url = "${user.service.url}", // Base URL for user-service (from application properties)
        configuration = FeignBasicAuthConfig.class // Configures basic auth for this client
)
public interface UserClient {

    // Sends a POST request to create a new user
    @RequestMapping(method = RequestMethod.POST, value = "/api/user/creat")
    void createUser(@RequestBody UserCreateRequest userCreateRequest);

    // Sends a PUT request to update the username of a user by ID
    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/{id}/username")
    void changeUsername(@PathVariable("id") Integer id,
                        @RequestParam("username") String username);

    // Sends a PUT request to mark a user as deleted by ID
    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/{id}")
    void deleteUser(@PathVariable("id") Integer id);
}
