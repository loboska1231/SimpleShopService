package org.project.shopservice.services;

import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.models.User;
import org.project.shopservice.repository.UserRepository;
import org.project.shopservice.security.utils.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TokensDto authenticateUser( UserAuthDto dto) {
        User user = userRepository.findByUsername(dto.username()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(passwordEncoder.matches(dto.password(), user.getPassword())){
            return generator(user);
        }
        else throw new IllegalArgumentException("Invalid username or password");
    }

    public TokensDto registrateUser( UserRegistrationDto dto) {
        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .role("USER")
                .password(passwordEncoder.encode(dto.password()))
                .username(dto.username())
                .build();

        return generator(user);
    }

    public TokensDto refreshToken(String refreshToken) {
        if(StringUtils.isNotBlank(refreshToken)){
            String username = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if(user.getRefreshToken().equals(refreshToken)) return generator(user);
            else throw new JwtException("Wrong token");
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Not found user with '%s'".formatted(username)));
    }

    public TokensDto generator(User user){
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);

        userRepository.save(user);
        return TokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}