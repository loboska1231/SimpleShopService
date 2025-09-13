package org.project.orderservice.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.orderservice.dtos.onRequest.users.UserAuthDto;
import org.project.orderservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.orderservice.dtos.onResponse.TokensDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public TokensDto authenticateUser(@Valid UserAuthDto dto) {
        return null;
    }

    public TokensDto registrateUser(@Valid UserRegistrationDto dto) {
        return null;
    }

    public TokensDto refreshToken(@Valid String refreshToken) {
        return null;

    }
}
