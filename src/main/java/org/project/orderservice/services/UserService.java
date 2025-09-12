package org.project.orderservice.services;

import jakarta.validation.Valid;
import org.project.orderservice.dtos.onRequest.users.UserAuthDto;
import org.project.orderservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.orderservice.dtos.onResponse.TokensDto;

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
