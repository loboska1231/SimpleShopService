package org.project.shopservice.services;

import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.project.shopservice.exceptions.UserAlreadyExistException;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.enums.Roles;
import org.project.shopservice.models.User;
import org.project.shopservice.repository.UserRepository;
import org.project.shopservice.security.utils.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TokensDto authenticateUser( UserAuthDto dto) {
        if(dto.isValid()){
            User user = userRepository.findByEmail(dto.email()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if(passwordEncoder.matches(dto.password(), user.getPassword())){
                return generator(user);
            }
        }
        throw new IllegalArgumentException("Invalid username or password");
    }

    public TokensDto registrateUser( UserRegistrationDto dto) {
        User user = null;
        if(!dto.hasEmptyRequiredFields()){
            String role = StringUtils.isBlank(dto.role()) ? "USER" : Roles.valueOf(dto.role().toUpperCase()).toString();
            user = User.builder()
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .roles(Set.of(role))
                    .password(passwordEncoder.encode(dto.password()))
                    .email(dto.email())
                    .build();
        }

        return generator(user);
    }

    public TokensDto refreshToken(String refreshToken) {
        if(StringUtils.isNotBlank(refreshToken)){
            String username = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if(user.getRefreshToken().equals(refreshToken)) return generator(user);
            else throw new JwtException("Wrong token");
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("Not found user with '%s'".formatted(username)));
    }

    @SneakyThrows
    private TokensDto generator(User user){
        if(user != null){
            if(userRepository.findByEmail(user.getEmail()).isPresent()){
                throw new UserAlreadyExistException("User with email " + user.getEmail() + " already exists");
            }
            else{
                String accessToken = jwtUtil.generateAccessToken(user);
                String refreshToken = jwtUtil.generateRefreshToken(user);
                user.setRefreshToken(refreshToken);

                userRepository.save(user);
                return TokensDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        } else throw new IllegalArgumentException("Object has Empty fields!");
    }
}