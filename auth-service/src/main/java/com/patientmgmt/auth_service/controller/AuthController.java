package com.patientmgmt.auth_service.controller;

import com.patientmgmt.auth_service.dto.LoginRequestDto;
import com.patientmgmt.auth_service.dto.LoginResponseDto;
import com.patientmgmt.auth_service.service.AuthService;
import com.patientmgmt.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "User login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Optional<String> token = authService.authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new LoginResponseDto(token.get()));
    }

    @Operation(summary = "Register user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequestDto loginRequestDto) {
        if (userService.existsByEmail(loginRequestDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }
        userService.register(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(summary = "Check if jwt token is valid")
    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestHeader("Authorization") String authHeader) {
        if (null == authHeader || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        return authService.isValidToken(token) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
