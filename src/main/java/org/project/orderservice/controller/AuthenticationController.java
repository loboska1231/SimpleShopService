package org.project.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.orderservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.orderservice.dtos.onRequest.users.UserAuthDto;
import org.project.orderservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.orderservice.dtos.onResponse.TokensDto;
import org.project.orderservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthenticationController {
    private final UserService userService;
    @PostMapping("/authentication")
    public ResponseEntity<TokensDto> authenticateUser(@Valid @RequestBody UserAuthDto dto) {
        return ResponseEntity.ok(userService.authenticateUser(dto));
    }
    @PostMapping("/registration")
    public ResponseEntity<TokensDto> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.ok(userService.registrateUser(dto));
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokensDto> refreshToken(@Valid @RequestBody String refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }
}
