package com.patientmgmt.auth_service.service;

import com.patientmgmt.auth_service.JwtUtil;
import com.patientmgmt.auth_service.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userServices;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userServices, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userServices = userServices;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(String email, String password) {
        return userServices.findByEmailId(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));
    }

    public boolean isValidToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException exception) {
            return false;
        }

    }
}
