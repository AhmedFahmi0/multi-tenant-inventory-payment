package com.payment.service;

import com.payment.dto.AuthRequest;
import com.payment.dto.AuthResponse;
import com.payment.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
    AuthResponse refreshToken(String refreshToken);
}
