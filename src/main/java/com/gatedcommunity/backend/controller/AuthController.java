package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.AuthDTO;
import com.gatedcommunity.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Backend is working!");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTO.AuthResponse> register(@RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));  // ✅ AuthDTO.RegisterRequest
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(@RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));     // ✅ AuthDTO.LoginRequest
    }

    @PostMapping("/register-tenant")
    public ResponseEntity<AuthDTO.AuthResponse> registerTenant(
            @RequestParam String token,
            @RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity.ok(authService.registerTenant(request, token));
    }
}