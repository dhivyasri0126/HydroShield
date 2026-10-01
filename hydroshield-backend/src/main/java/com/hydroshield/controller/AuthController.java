package com.hydroshield.controller;

import com.hydroshield.dto.AuthDTOs.*;
import com.hydroshield.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        if ("Invalid email or password".equals(response.getMessage())) {
            return ResponseEntity.status(401).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-public")
    public ResponseEntity<AuthResponse> registerPublicUser(@RequestBody PublicRegisterRequest request) {
        AuthResponse response = authService.registerPublicUser(request);
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-researcher")
    public ResponseEntity<AuthResponse> registerResearcher(@RequestBody ResearcherRegisterRequest request) {
        AuthResponse response = authService.registerResearcher(request);
        if (response.getUserId() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
