package org.project.orderservice.security.filters;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException { // ### 2 ###
		String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION); // ### 4 ###

		if(StringUtils.isNotBlank(authHeaderValue) && authHeaderValue.startsWith("Bearer ")){
			String token = authHeaderValue.substring(7);
		}

		filterChain.doFilter(request, response);
	}
}
