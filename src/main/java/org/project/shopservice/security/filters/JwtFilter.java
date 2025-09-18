package org.project.shopservice.security.filters;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.security.utils.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException { // ### 2 ###
		String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION); // ### 4 ###

		if(StringUtils.isBlank(authHeaderValue) ){
			filterChain.doFilter(request, response);
			return;
		}
		String token = authHeaderValue.substring(7);
		try{
			if (!jwtUtil.isExpired(token)) {
				filterChain.doFilter(request, response);
				return;
			}

			String username = jwtUtil.extractUsername(token);
			UserDetails user = userDetailsService.loadUserByUsername(username);
			List<GrantedAuthority> authorities = jwtUtil.extractFromToken(token, claims -> claims.get("roles",List.class))
					.stream()
					.map(role-> new SimpleGrantedAuthority("ROLE_%s".formatted(role.toString())))
					.toList();
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception e){
			throw new AuthorizationDeniedException("denied");
		} finally {
			filterChain.doFilter(request, response);

		}
	}
}
