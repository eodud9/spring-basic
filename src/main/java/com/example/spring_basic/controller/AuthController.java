package com.example.spring_basic.controller;

import com.example.spring_basic.dto.LoginRequest;
import com.example.spring_basic.dto.LoginResponse;
import com.example.spring_basic.dto.LogoutRequest;
import com.example.spring_basic.dto.RefreshRequest;
import com.example.spring_basic.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest request){
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request){
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
