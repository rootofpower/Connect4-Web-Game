package sk.tuke.gamestudio.server.controller.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sk.tuke.gamestudio.dto.requests.LoginRequest;
import sk.tuke.gamestudio.dto.requests.RegisterRequest;
import sk.tuke.gamestudio.dto.responses.AuthResponse;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.Role;
import sk.tuke.gamestudio.service.interfaces.AuthService;
import sk.tuke.gamestudio.service.interfaces.UserService;

@RestController
@RequestMapping("/api/connect4/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Attempting registration for username: {}", request.getUsername());

        if (userService.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username '{}' already exists.", request.getUsername());
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email '{}' already exists.", request.getEmail());
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setRole(Role.USER);

        try {
            userService.saveUser(newUser);
            log.info("User '{}' registered successfully.", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (Exception e) {
            log.error("Error during user registration for username '{}': {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        log.info("Attempting login for identifier: {}", request.getIdentifier());

        try {
            AuthResponse authResponse = authService.login(request);
            log.info("User '{}' logged in successfully.", request.getIdentifier());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            log.warn("Login failed for identifier '{}': Invalid credentials", request.getIdentifier());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid credentials");
        } catch (AuthenticationException e) {
            log.error("Authentication failed for identifier '{}': {}", request.getIdentifier(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Authentication failed");
        } catch (Exception e) {
            log.error("Error during login for identifier '{}': {}", request.getIdentifier(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login.");
        }
    }
}
