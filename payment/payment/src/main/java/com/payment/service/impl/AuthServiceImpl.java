package com.payment.service.impl;

import com.payment.dto.AuthRequest;
import com.payment.dto.AuthResponse;
import com.payment.dto.RegisterRequest;
import com.payment.exception.AuthenticationException;
import com.payment.model.Role;
import com.payment.model.User;
import com.payment.repository.UserRepository;
import com.payment.security.JwtService;
import com.payment.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username is already taken");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        // Save user
        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AuthenticationException("User not found"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        
            // Generate token
            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Extract email from refresh token
        String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            throw new AuthenticationException("Invalid refresh token");
        }

        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));
                
        // Verify the refresh token
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new AuthenticationException("Invalid refresh token");
        }
        
        // Generate new access token
        String newAccessToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(newAccessToken)
                .build();
    }
}
