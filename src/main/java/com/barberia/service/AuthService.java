package com.barberia.service;

import com.barberia.dto.AuthResponse;
import com.barberia.dto.LoginRequest;
import com.barberia.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    void logout(String token);
    AuthResponse refreshToken(String refreshToken);
    void solicitarRecuperacionPassword(String email);
    void recuperarPassword(String token, String newPassword);
}

