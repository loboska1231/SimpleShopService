package org.project.shopservice.services;

import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.enums.Roles;
import org.project.shopservice.models.User;
import org.project.shopservice.repository.UserRepository;
import org.project.shopservice.security.utils.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;

	private User user;

	@BeforeEach
	void setUp(){

		when(passwordEncoder.encode(anyString())).thenReturn(new BCryptPasswordEncoder().encode("12345"));
		user = User.builder()
				.id("0x00000001")
				.email("test@testing.com")
				.firstName("test")
				.password(passwordEncoder.encode("12345"))
				.lastName("testing")
				.roles(Set.of("USER"))
				.build();
	}

	static Stream<Arguments> registrateUserThrows(){
		return Stream.of(
				Arguments.of(UserRegistrationDto
						.builder()
						.email(" ")
						.password(" ")
						.firstName("test")
						.lastName("testing")
								.role("USER")
						.build()),
				Arguments.of(UserRegistrationDto
						.builder()
						.email(" ")
						.password(" ")
						.firstName(" ")
						.lastName(" ")
								.role("ADmin")
						.build()),
				Arguments.of(UserRegistrationDto
						.builder()
						.email(" ")
						.password(" ")
						.firstName(" ")
						.lastName(" ")
						.build()),
				Arguments.of(UserRegistrationDto
						.builder()
						.email(" ")
						.password(" ")
						.firstName(" ")
						.lastName(" ")
								.role(" ")
						.build())
		);
	}

	@ParameterizedTest
	@MethodSource("registrateUserThrows")
	public void testRegistateUser_throwsIllegalArg(UserRegistrationDto dto){
		assertTrue(dto.hasEmptyRequiredFields());
		assertThrowsExactly(IllegalArgumentException.class,()-> authService.registrateUser(dto));
		verifyNoInteractions(userRepository);
	}

	static Stream<Arguments> registrateUserDoesNotThrows(){
		return Stream.of(
				Arguments.of(
						UserRegistrationDto
						.builder()
						.email("test@testing.com")
						.password("12345")
						.firstName("test")
						.lastName("testing")
						.role("USER")
						.build()),
				Arguments.of(
						UserRegistrationDto
						.builder()
						.email("test@testing.com")
						.password("12345")
						.firstName("test")
						.lastName("testing")
						.role("ADmIn")
						.build()),
				Arguments.of(
						UserRegistrationDto
						.builder()
						.email("test@testing.com")
						.password("12345")
						.firstName("test")
						.lastName("testing")
						.build())
		);
	}

	@ParameterizedTest
	@MethodSource("registrateUserDoesNotThrows")
	public void testRegistateUser(UserRegistrationDto dto){

		assertDoesNotThrow(()-> authService.registrateUser(dto));
		assertFalse(dto.hasEmptyRequiredFields());
		verify(userRepository).save(any(User.class));
		verifyNoMoreInteractions(userRepository);
	}
	@Test
	public void testAuthenticateUser(){
		UserAuthDto userAuthDto = new UserAuthDto("test@testing.com","12345");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(anyString(),anyString())).thenReturn(new BCryptPasswordEncoder().matches(userAuthDto.password(), user.getPassword()));

		assertDoesNotThrow(()-> authService.authenticateUser(userAuthDto));
		verify(userRepository).save(any(User.class));
	}

	static Stream<Arguments> authenticateUserThrowsIllegalArgumentException(){
		return Stream.of(
				Arguments.of(new UserAuthDto(" "," ")),
				Arguments.of(new UserAuthDto("","")),
				Arguments.of(new UserAuthDto(" ","12345"))
		);
	}

	@ParameterizedTest
	@MethodSource("authenticateUserThrowsIllegalArgumentException")
	public void testAuthenticateUser_throwsIllegalArgumentException(UserAuthDto dto){
		assertThrowsExactly(IllegalArgumentException.class, ()-> authService.authenticateUser(dto));
		verifyNoInteractions(userRepository);
	}
	static Stream<Arguments> authenticateUserThrowsUsernameNotFound(){
		return Stream.of(
				Arguments.of(new UserAuthDto(" test@testing.com","12345")),
				Arguments.of(new UserAuthDto("test","12345")),
				Arguments.of(new UserAuthDto("test@testing.com ","12345"))
		);
	}
	@ParameterizedTest
	@MethodSource("authenticateUserThrowsUsernameNotFound")
	public void testAuthenticateUser_throwsUsernameNotFound(UserAuthDto dto){
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		lenient().when(userRepository.findByEmail(eq("test@tesing.com"))).thenReturn(Optional.of(user));

		assertThrowsExactly(UsernameNotFoundException.class, ()-> authService.authenticateUser(dto));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testRefreshToken(){
		var dto = UserRegistrationDto
				.builder()
				.email("test@testing.com")
				.password("12345")
				.firstName("test")
				.lastName("testing")
				.build();
		var tokens = authService.registrateUser(dto);

		assertNull(tokens);
	}
}