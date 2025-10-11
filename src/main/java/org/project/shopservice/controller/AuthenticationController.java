package org.project.shopservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.users.RefreshToken;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthService authService;
    @PostMapping("/signin")
    public ResponseEntity<TokensDto> authenticateUser(@Valid @RequestBody UserAuthDto dto) {
        return ResponseEntity.ok(authService.authenticateUser(dto));
    }
    @PostMapping("/signup")
    public ResponseEntity<TokensDto> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.ok(authService.registrateUser(dto));
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokensDto> refreshToken(@Valid @RequestBody RefreshToken refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken.refreshToken())); // =D
    }
}
