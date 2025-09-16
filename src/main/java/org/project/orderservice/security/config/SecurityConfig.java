package org.project.orderservice.security.config;

import lombok.RequiredArgsConstructor;
import org.project.orderservice.security.filters.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{ // ### 1 ###
		return http
				.csrf(CsrfConfigurer::disable)
				.authorizeHttpRequests(request->
						request.requestMatchers("/orders","/orders/**").authenticated()
								.requestMatchers("/api/**", "/error").permitAll()
								.requestMatchers(HttpMethod.GET,"/products","/products/**").permitAll()
								.anyRequest().authenticated()
				)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter(), AuthorizationFilter.class)
				.build();
	}

	@Bean
	public JwtFilter jwtFilter(){ // ### 3 ###
		return new JwtFilter();
	}
	

}
