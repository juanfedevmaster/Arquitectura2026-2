package com.tienda.auth.service;

import com.tienda.auth.dto.LoginRequest;
import com.tienda.auth.dto.LoginResponse;
import com.tienda.auth.dto.RegisterRequest;
import com.tienda.auth.dto.RefreshRequest;
import com.tienda.auth.model.User;
import com.tienda.auth.repository.UserRepository;
import com.tienda.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!user.isActive()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String userId = user.getId().toString();
        String role = user.getRole().name();
        return new LoginResponse(
                jwtUtil.generateAccessToken(userId, user.getUsername(), role),
                jwtUtil.generateRefreshToken(userId, user.getUsername(), role)
        );
    }

    public LoginResponse refresh(RefreshRequest request) {
        if (!jwtUtil.isValid(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }
        var claims = jwtUtil.parseToken(request.getRefreshToken());
        String userId = claims.getSubject();
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        return new LoginResponse(
                jwtUtil.generateAccessToken(userId, username, role),
                jwtUtil.generateRefreshToken(userId, username, role)
        );
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username ya en uso");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya en uso");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
