package com.labrox.iam.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.iam.dto.LoginRequest;
import com.labrox.iam.dto.LoginResponse;
import com.labrox.iam.dto.SwitchCentreRequest;
import com.labrox.iam.service.AuthenticationService;
import com.labrox.iam.service.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam")
@Tag(name = "Authentication & Access Control")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT with tenant context")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }
}

