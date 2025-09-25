package org.project.shopservice.security.config;

import lombok.RequiredArgsConstructor;
import org.project.shopservice.security.filters.JwtFilter;
import org.project.shopservice.security.utils.JwtUtil;
import org.project.shopservice.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{ // ### 1 ###
		return http
				.csrf(CsrfConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(request->
						request.requestMatchers("/orders","/orders/**").authenticated()
								.requestMatchers("/auth/**", "/error").permitAll()
								.requestMatchers(HttpMethod.GET,"/products","/products/**").permitAll()
								.anyRequest().authenticated()
				)
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter(), AuthorizationFilter.class)
				.build();
	}

	@Bean
	public JwtFilter jwtFilter(){ // ### 3 ###
		return new JwtFilter(jwtUtil, authService);
	}
	@Bean
	public AuthenticationProvider authenticationProvider(){
		DaoAuthenticationProvider dao = new DaoAuthenticationProvider(authService);
		dao.setPasswordEncoder(passwordEncoder);
		return dao;
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
			throws Exception {
		return config.getAuthenticationManager();
	}
}
