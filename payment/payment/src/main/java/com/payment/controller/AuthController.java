package com.payment.controller;

import com.payment.dto.AuthRequest;
import com.payment.dto.AuthResponse;
import com.payment.dto.RegisterRequest;
import com.payment.model.Role;
import com.payment.model.User;
import com.payment.repository.UserRepository;
import com.payment.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        System.out.println("Register request received for username: " + request.getUsername());
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(AuthResponse.builder()
                                .error("Username is already taken")
                                .build());
            }

            var user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .isActive(true)
                    .role(Role.USER)
                    .build();
            
            userRepository.save(user);
            
            var jwtToken = jwtService.generateToken(user);
            
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(jwtToken)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .error("Registration failed: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            var user = (User) authentication.getPrincipal();
            var jwtToken = jwtService.generateToken(user);
            
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(jwtToken)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}
