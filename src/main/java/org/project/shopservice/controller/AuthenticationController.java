package org.project.shopservice.controller;

import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.users.RefreshToken;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<TokensDto> authenticateUser(@Validated @RequestBody UserAuthDto dto) {
        return ResponseEntity.ok(authService.authenticateUser(dto));
    }
    @PostMapping("/signup")
    public ResponseEntity<TokensDto> registerUser(@Validated @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.ok(authService.registrateUser(dto));
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokensDto> refreshToken(@Validated @RequestBody RefreshToken refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken.refreshToken())); // =D
    }
}
